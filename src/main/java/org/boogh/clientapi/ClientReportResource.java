package org.boogh.clientapi;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Stopwatch;
import com.vividsolutions.jts.geom.Geometry;
import io.github.jhipster.service.filter.LongFilter;
import org.boogh.clientservice.*;
import org.boogh.clientservice.dto.ReportCriteria;
import org.boogh.clientservice.dto.ReportDTO;
import org.boogh.clientservice.mapper.ReportMapper;
import org.boogh.config.ApplicationProperties;
import org.boogh.config.Constants;
import org.boogh.domain.Report;
import org.boogh.domain.ReportStatus;
import org.boogh.domain.User;
import org.boogh.domain.enumeration.ReportState;
import org.boogh.domain.enumeration.ReportStatusState;
import org.boogh.web.rest.errors.*;
import org.boogh.web.rest.util.GeoUtils;
import org.boogh.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * REST controller for managing Report.
 */
@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientReportResource {

    private final Logger log = LoggerFactory.getLogger(ClientReportResource.class);

    private static final String ENTITY_NAME = "report";

    private final Integer NUM_REPORTS_PER_PAGE = 5;

    private final ClientReportService reportService;

    private final ClientReportQueryService reportQueryService;

    private final ReportMapper reportMapper;

    private final CheckAuth checkAuth;

    private final S3Wrapper s3Wrapper;


    public ClientReportResource(ClientReportService reportService, ClientReportQueryService reportQueryService, ReportMapper reportMapper, CheckAuth checkAuth, ApplicationProperties applicationProperties) {
        this.reportService = reportService;
        this.reportQueryService = reportQueryService;
        this.reportMapper = reportMapper;
        this.checkAuth = checkAuth;
        this.s3Wrapper = new S3Wrapper(applicationProperties);
    }

    /**
     * POST  /reports : Create a new report.
     *
     * @param report the report to create
     * @return the ResponseEntity with status 201 (Created) and with body the new report, or with status 400 (Bad Request) if the report has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/reports")
    @Timed
    @Transactional( timeout = 120)
    public ResponseEntity<ReportDTO> createReport(@Valid @RequestBody Report report) throws URISyntaxException, IOException {
        log.debug("REST request to save Report : {}", report);
        if (report.getId() != null) {
            throw new BadRequestAlertException("A new report cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (report.getLatitude() == 0 && report.getLongitude() == 0) {
            throw new ReportLocationMissingException();
        }

        if (report.getTitle() == null || report.getTitle().equals("")) {
            throw new ReportTitleMissingException();
        }

        if (report.getDescription() == null || report.getDescription().equals("")) {
            throw new ReportDescriptionMissingException();
        }

        boolean hasAuthority;

        if (report.getReporter().getId() != null) {
            hasAuthority = checkAuth.hasAuthority(report.getReporter().getId());
        } else {
            hasAuthority = true;
        }

        Report parent = report.getParent();

        if (parent != null) {
            // Find more details about the parent report
            Optional<Report> detailedParent = reportService.findOne(parent.getId());

            if (!detailedParent.isPresent()) {
                throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid update");
            }

            Report detailedParentReport = detailedParent.get();

            if (detailedParentReport.getReporter() != null) {
                // Only allow users to update reports that they have issued
                hasAuthority = checkAuth.hasAuthority(detailedParentReport.getReporter().getId());
            } else {
                // Restrict users from updating reports that have null reporters
                hasAuthority = false;
            }
        }

        if (!hasAuthority) {
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }

        // Sanitize user input
        HtmlEscaper.escapeReport(report);

        // Ensure the report state is set to PENDING before saving it to the db
        report.setState(ReportState.PENDING);
        report.setDate(LocalDate.now());
        report.setLocation(GeoUtils.pointFromLongLat(report.getLongitude(), report.getLatitude()));

        if (report.getReporter().getId() == null){
            report.setReporter(null);
        }

        ReportDTO result = reportMapper.reportToReportDTO(reportService.save(report));

        List<File> images = ImageProcessor.base64ToFile(result.getImages(), result.getId());
        s3Wrapper.uploadImages(images);
        ImageProcessor.cleanUp(images);

        return ResponseEntity.created(new URI("/clientapi/reports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reports : Updates an existing report.
     *
     * @param report the report to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated report,
     * or with status 400 (Bad Request) if the report is not valid,
     * or with status 500 (Internal Server Error) if the report couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/reports")
    @Timed
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    public ResponseEntity<ReportDTO> updateReport(@Valid @RequestBody Report report) throws URISyntaxException {
        log.debug("REST request to update Report : {}", report);
        if (report.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        report.setLocation(GeoUtils.pointFromLongLat(report.getLongitude(), report.getLatitude()));
        ReportDTO result = reportMapper.reportToReportDTO(reportService.save(report));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, report.getId().toString()))
            .body(result);
    }


    /**
     * GET  /reports : get all the reports.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     */
    @GetMapping("/reports")
    @Transactional
    @Timed
    public ResponseEntity<List<ReportDTO>> getAllReports(ReportCriteria criteria) throws IOException {
        log.debug("REST request to get Reports by criteria: {}", criteria);
        //By Default this should return all Reports with an approved status.
        LongFilter reporterId = criteria.getReporterId();

        Long id = null;
        if (reporterId != null) {
            id = reporterId.getEquals();
        }

        boolean hasAuthority = false;
        if (id != null){
            hasAuthority = checkAuth.hasAuthority(id);
        }

        List<Report> entityList = reportQueryService.findByCriteria(criteria, hasAuthority);
        List<Report> validEntities = new ArrayList<>();
        validEntities.addAll(entityList);

        //Remove reports that are updates.
        for (Report report: entityList){
            if (report.getParent() != null){
                validEntities.remove(report);
            }
        }

        checkIfFavoritedByCurrentUser(entityList);
        checkIfCurrentUserReport(entityList);
        setReportImages(entityList);

        List<ReportDTO> dtoList = reportMapper.reportToReportDTOs(validEntities);
        return ResponseEntity.ok().body(dtoList);
    }

    /**
     * GET  /reports : get all the reports in bounding box.
     *
     * @param west x coordinate of lower point in bounding box.
     * @param south y coordinate of lower point in bounding box.
     * @param east x coordinate of upper point in bounding box.
     * @param north y coordinate of upper point in bounding box.
     * Where (x,y) = (longitude, latitude).
     * @return the ResponseEntity with status 200 (OK) and the list of reports in body
     */
    @GetMapping(value = "/reports", params = {"west", "south", "east", "north"})
    @Transactional
    @Timed
    public ResponseEntity<List<ReportDTO>> getReportsByBox(@RequestParam double west,
                                                           @RequestParam double south,
                                                           @RequestParam double east,
                                                           @RequestParam double north){
        log.debug("REST request to get a page of Reports by bounding box west {}, south {}, east {}, north {}", west, south, east, north);
        final Stopwatch stopwatch = Stopwatch.createStarted();

        Geometry boundingBox = GeoUtils.boundingBoxToGeometry(west, south, east, north);

        List<Report> entityList =  reportService.findWithinGeometry(boundingBox);
        checkIfFavoritedByCurrentUser(entityList);
        checkIfCurrentUserReport(entityList);
        List<ReportDTO> reports = reportMapper.reportToReportDTOs(entityList);
        log.info("\t" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " milliseconds");

        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    /**
     * GET  /reports : get all reports sorted by id desc.
     *
     * @return the ResponseEntity with status 200 (OK) and the count in body
     */
    @GetMapping(value = "/reports/sorted")
    @Transactional
    @Timed
    public ResponseEntity<List<ReportDTO>> getAllReportsByIdDesc(@RequestParam Integer page) throws IOException {
        log.debug("REST request to get all reports sorted by id desc");
        List<Report> entityList = reportService.findByIdDesc();
        Integer startIndex = page * NUM_REPORTS_PER_PAGE;
        if (entityList.size() > startIndex) {
            entityList = entityList.subList(startIndex, Math.min(startIndex + NUM_REPORTS_PER_PAGE, entityList.size()));
            checkIfFavoritedByCurrentUser(entityList);
            checkIfCurrentUserReport(entityList);
            setReportImages(entityList);
            List<ReportDTO> reports = reportMapper.reportToReportDTOs(entityList);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);

    }

    /**
     * GET  /reports/:id : get the "id" report.
     *
     * @param id the id of the report to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the report, or with status 404 (Not Found)
     */
    @GetMapping("/reports/{id}")
    @Transactional
    @Timed
    public ResponseEntity<List<ReportDTO>> getReport(@PathVariable Long id) throws IOException {
        log.debug("REST request to get Report : {}", id);
        Optional<Report> report = reportService.findOne(id);
        if (!report.isPresent()) {
            throw new BadRequestAlertException("Report not found", ENTITY_NAME, "Not found");
        }

        User user = report.get().getReporter();
        boolean hasAuthority = false;
        if (user != null) {
            hasAuthority = checkAuth.hasAuthority(user.getId());
        }

        if (report.get().getState() != ReportState.APPROVED && !hasAuthority) {
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }

        List<Report> reportPlusUpdates = reportService.findReport(id, hasAuthority);
        checkIfFavoritedByCurrentUser(reportPlusUpdates);
        checkIfCurrentUserReport(reportPlusUpdates);
        setReportImages(reportPlusUpdates);

        return new ResponseEntity<>(reportMapper.reportToReportDTOs(reportPlusUpdates), HttpStatus.OK);
    }

    /**
    * GET  /reports/count : count all the reports.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/reports/count")
    @Timed
    public ResponseEntity<Long> countReports(ReportCriteria criteria) {
        log.debug("REST request to count Reports by criteria: {}", criteria);
        return ResponseEntity.ok().body(reportQueryService.countByCriteria(criteria));
    }

    /**
     * GET /reports/pages : number pages of all approved reports.
     *
     * @return the ResponseEntity with status 200 (OK) and the number of pages in body
     */
    @GetMapping("reports/pages")
    public ResponseEntity<Long> numReportPages() {
        log.debug("REST request to find number of report pages");
        List<Report> entityList = reportService.findByIdDesc();
        double size = entityList.size();
        long numReportPages = (long) Math.ceil(size / NUM_REPORTS_PER_PAGE);
        return ResponseEntity.ok().body(numReportPages);
    }

    /**
     * For each report in entityList determine if it's favorited by the current user.
     * @param entityList
     */
    private void checkIfFavoritedByCurrentUser(List<Report> entityList){
        for(Report report: entityList){
            Boolean favorited = false;
            for(ReportStatus reportStatus: report.getReportStatuses()){
                // If current user has favorited this report, checkAuth.hasAuthority will return true
                if(checkAuth.hasAuthority(reportStatus.getReporter().getId()) && (reportStatus.getSaved() == ReportStatusState.TRUE)){
                    favorited = true;
                    break;
                }
            }
            report.setFavoritedByCurrentUser(favorited);
        }
    }

    /**
     * For each report in entityList determine if it's reporter is the current user.
     * @param entityList
     */
    private void checkIfCurrentUserReport(List<Report> entityList){
        for (Report report: entityList) {
            User reporter = report.getReporter();
            if (reporter != null && checkAuth.hasAuthority(report.getReporter().getId())) {
                report.setCurrentUsersReport(true);
            }
        }
    }

    /**
     * Retrieve and set images from S3 for each report in entityList.
     * @param entityList
     * @throws IOException
     */
    private void setReportImages(List<Report> entityList) throws IOException {
        List<String> allImageNames = s3Wrapper.listObjects();
        for (Report report: entityList) {
            List<String> imageNames = s3Wrapper.findReportImageNames(allImageNames, report.getId());
            report.setImages(imageNames);
        }
    }

    /**
     * DELETE  /reports/:id : delete the "id" report.
     *
     * @param id the id of the report to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/reports/{id}")
    @Timed
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        log.debug("REST request to delete Report : {}", id);
        Optional<Report> report = reportService.findOne(id);

        boolean hasAuthority = false;

        if (report.get().getReporter() != null) {
            hasAuthority = checkAuth.hasAuthority(report.get().getReporter().getId());
        }

        if (!hasAuthority) {
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }

        // Get the report images from S3
        List<String> allImageKeys = s3Wrapper.listObjects();
        List<String> reportImageKeys = s3Wrapper.findReportImageNames(allImageKeys, id);

        for (String key: reportImageKeys) {
            s3Wrapper.deleteObject(key);
        }

        reportService.delete(id);

        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
