package org.boogh.clientapi;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.service.filter.LongFilter;
import org.boogh.clientservice.CheckAuth;
import org.boogh.clientservice.ClientReportStatusQueryService;
import org.boogh.clientservice.ClientReportStatusService;
import org.boogh.clientservice.S3Wrapper;
import org.boogh.clientservice.dto.ReportStatusCriteria;
import org.boogh.clientservice.dto.ReportStatusDTO;
import org.boogh.clientservice.mapper.ReportStatusMapper;
import org.boogh.config.ApplicationProperties;
import org.boogh.config.Constants;
import org.boogh.domain.Report;
import org.boogh.domain.ReportStatus;
import org.boogh.domain.User;
import org.boogh.domain.enumeration.ReportState;
import org.boogh.domain.enumeration.ReportStatusState;
import org.boogh.service.ReportService;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing ReportStatus.
 */
@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientReportStatusResource {

    private final Logger log = LoggerFactory.getLogger(ClientReportStatusResource.class);

    private static final String ENTITY_NAME = "reportStatus";

    private final ClientReportStatusService reportStatusService;

    private final ClientReportStatusQueryService reportStatusQueryService;

    private final CheckAuth checkAuth;

    private final S3Wrapper s3Wrapper;

    private final ReportStatusMapper reportStatusMapper;

    private final ReportService reportService;

    public ClientReportStatusResource(ClientReportStatusService reportStatusService, ClientReportStatusQueryService reportStatusQueryService, CheckAuth checkAuth,
                                      ReportStatusMapper reportStatusMapper, ReportService reportService, ApplicationProperties applicationProperties) {
        this.reportStatusService = reportStatusService;
        this.reportStatusQueryService = reportStatusQueryService;
        this.checkAuth = checkAuth;
        this.s3Wrapper = new S3Wrapper(applicationProperties);
        this.reportStatusMapper = reportStatusMapper;
        this.reportService = reportService;
    }

    /**
     * POST  /report-statuses : Create a new reportStatus.
     *
     * @param reportStatus the reportStatus to create
     * @return the ResponseEntity with status 201 (Created) and with body the new reportStatus, or with status 400 (Bad Request) if the reportStatus has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/report-statuses")
    @Timed
    public ResponseEntity<ReportStatusDTO> createReportStatus(@Valid @RequestBody ReportStatus reportStatus) throws URISyntaxException {
        log.debug("REST request to save ReportStatus : {}", reportStatus);
        if (reportStatus.getId() != null) {
            throw new BadRequestAlertException("A new reportStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (!checkAuth.hasAuthority(reportStatus.getReporter().getId())) {
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }

        // Make a user can only favorite a Pending/Rejected report iff they are the author.
        Long reportId = reportStatus.getReport().getId();
        Report report = reportService.findOne(reportId).get();
        if (!report.getState().equals(ReportState.APPROVED) && !checkAuth.hasAuthority(report.getReporter().getId())) {
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid credentials");
        }

        ReportStatusDTO result = reportStatusMapper.reportStatusToReportStatusDTO(reportStatusService.save(reportStatus));
        return ResponseEntity.created(new URI("/clientapi/report-statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /report-statuses : Updates an existing reportStatus.
     *
     * @param reportStatus the reportStatus to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated reportStatus,
     * or with status 400 (Bad Request) if the reportStatus is not valid,
     * or with status 500 (Internal Server Error) if the reportStatus couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/report-statuses")
    @Timed
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    public ResponseEntity<ReportStatusDTO> updateReportStatus(@Valid @RequestBody ReportStatus reportStatus) throws URISyntaxException {
        log.debug("REST request to update ReportStatus : {}", reportStatus);
        if (reportStatus.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ReportStatusDTO result = reportStatusMapper.reportStatusToReportStatusDTO(reportStatusService.save(reportStatus));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, reportStatus.getId().toString()))
            .body(result);
    }

    /**
     * GET  /report-statuses : get all the reportStatuses.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of reportStatuses in body
     */
    @GetMapping("/report-statuses")
    @Transactional
    @Timed
    public ResponseEntity<List<ReportStatusDTO>> getAllReportStatuses(ReportStatusCriteria criteria) throws IOException {
        log.debug("REST request to get ReportStatuses by criteria: {}", criteria);
        LongFilter longFilter = criteria.getReporterId();
        if(longFilter == null){
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Missing Reporter ID");
        }
        if(!checkAuth.hasAuthority(longFilter.getEquals())){
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }
        List<ReportStatus> entityList = reportStatusQueryService.findByCriteria(criteria);
        if(criteria.getSaved() != null && criteria.getSaved().getEquals().equals(ReportStatusState.TRUE)){
            favoriteReports(entityList);
            checkIfCurrentUserReport(entityList);
            setReportImages(entityList);
        }
        List<ReportStatusDTO> dtoList = reportStatusMapper.reportStatusesToReportStatusDTOs(entityList);
        return ResponseEntity.ok().body(dtoList);
    }

    /**
    * GET  /report-statuses/count : count all the reportStatuses.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/report-statuses/count")
    @Timed
    public ResponseEntity<Long> countReportStatuses(ReportStatusCriteria criteria) {
        log.debug("REST request to count ReportStatuses by criteria: {}", criteria);
        return ResponseEntity.ok().body(reportStatusQueryService.countByCriteria(criteria));
    }

    /**
     * Set isFavoritedByCurrentUser field to true for each of the report statuses in entityList
     * @param entityList
     */
    private void favoriteReports(List<ReportStatus> entityList){
        for(ReportStatus reportStatus: entityList){
            reportStatus.getReport().setFavoritedByCurrentUser(true);
        }
    }

    /**
     * For each report in entityList determine if it's reporter is the current user.
     * @param entityList
     */
    private void checkIfCurrentUserReport(List<ReportStatus> entityList){
        for (ReportStatus reportStatus: entityList) {
            User reporter = reportStatus.getReport().getReporter();
            if ((reporter != null) && checkAuth.hasAuthority(reporter.getId())) {
                reportStatus.getReport().setCurrentUsersReport(true);
            }
        }
    }

    /**
     * Retrieve and set images from S3 for each of the report statuses in entityList.
     * @param entityList
     * @throws IOException
     */
    private void setReportImages(List<ReportStatus> entityList) throws IOException {
        List<String> allImageNames = s3Wrapper.listObjects();
        for (ReportStatus reportStatus: entityList) {
            List<String> imageNames = s3Wrapper.findReportImageNames(allImageNames, reportStatus.getReport().getId());
            reportStatus.getReport().setImages(imageNames);
        }
    }
}
