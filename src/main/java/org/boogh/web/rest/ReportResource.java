package org.boogh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.boogh.clientservice.S3Wrapper;
import org.boogh.clientservice.TelegramBotService;
import org.boogh.config.ApplicationProperties;
import org.boogh.domain.Report;
import org.boogh.domain.TelegramChat;
import org.boogh.domain.User;
import org.boogh.domain.enumeration.ReportState;
import org.boogh.repository.TelegramChatRepository;
import org.boogh.service.MailService;
import org.boogh.service.ReportQueryService;
import org.boogh.service.ReportService;
import org.boogh.service.dto.ReportCriteria;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.util.GeoUtils;
import org.boogh.web.rest.util.HeaderUtil;
import org.boogh.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Report.
 */
@RestController
@RequestMapping("/api")
public class ReportResource {

    private final Logger log = LoggerFactory.getLogger(ReportResource.class);

    private static final String ENTITY_NAME = "report";

    private final ReportService reportService;

    private final ReportQueryService reportQueryService;

    private final MailService mailService;

    private final S3Wrapper s3Wrapper;

    private final TelegramChatRepository telegramChatRepository;

    private final TelegramBotService telegramBotService;

    public ReportResource(ReportService reportService, ReportQueryService reportQueryService, MailService mailService,
                          ApplicationProperties applicationProperties, TelegramChatRepository telegramChatRepository, TelegramBotService telegramBotService) {
        this.reportService = reportService;
        this.reportQueryService = reportQueryService;
        this.mailService = mailService;
        this.s3Wrapper = new S3Wrapper(applicationProperties);
        this.telegramChatRepository = telegramChatRepository;
        this.telegramBotService = telegramBotService;
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
    public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) throws URISyntaxException {
        log.debug("REST request to save Report : {}", report);
        if (report.getId() != null) {
            throw new BadRequestAlertException("A new report cannot already have an ID", ENTITY_NAME, "idexists");
        }
        report.setLocation(GeoUtils.pointFromLongLat(report.getLongitude(), report.getLatitude()));
        report.setDate(LocalDate.now());
        Report result = reportService.save(report);
        return ResponseEntity.created(new URI("/api/reports/" + result.getId()))
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
    public ResponseEntity<Report> updateReport(@Valid @RequestBody Report report) throws IOException {
        log.debug("REST request to update Report : {}", report);

        if (report.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Report reportToBeUpdated = reportService.findOne(report.getId()).get();
        report.setLocation(GeoUtils.pointFromLongLat(report.getLongitude(), report.getLatitude()));
        Report result = reportService.save(report);

        ReportState reportState = result.getState();
        User reporter = result.getReporter();

        if (reporter != null && reportState != reportToBeUpdated.getState() && !reportState.equals(ReportState.PENDING)) {
            List<TelegramChat> chats = telegramChatRepository.findTelegramChatByUserId(reporter.getId());
            if (chats.isEmpty()) {
                mailService.sendReportEmail(reporter, result, reportState);
            } else {
                // Send update via telegram chat id
                TelegramChat telegramChat = chats.get(0);
                telegramBotService.sendReportMessage(result, reportState, telegramChat.getChatId());
            }
        }

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
    @Timed
    public ResponseEntity<List<Report>> getAllReports(ReportCriteria criteria, Pageable pageable) {

        Page<Report> page = reportQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reports");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
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
     * GET  /reports/:id : get the "id" report.
     *
     * @param id the id of the report to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the report, or with status 404 (Not Found)
     */
    @GetMapping("/reports/{id}")
    @Timed
    public ResponseEntity<Report> getReport(@PathVariable Long id) throws IOException {
        log.debug("REST request to get Report : {}", id);
        Optional<Report> report = reportService.findOne(id);
        setReportImages(report.get());
        return ResponseUtil.wrapOrNotFound(report);
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
        reportService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * Retrieve and set images from S3 for each report in entityList.
     * @param report
     * @throws IOException
     */
    private void setReportImages(Report report) throws IOException {
        List<String> allImageNames = s3Wrapper.listObjects();
        List<String> imageNames = s3Wrapper.findReportImageNames(allImageNames, report.getId());
        report.setImages(imageNames);
    }
}
