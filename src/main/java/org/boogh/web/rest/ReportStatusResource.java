package org.boogh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.boogh.domain.ReportStatus;
import org.boogh.service.ReportStatusQueryService;
import org.boogh.service.ReportStatusService;
import org.boogh.service.dto.ReportStatusCriteria;
import org.boogh.web.rest.errors.BadRequestAlertException;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ReportStatus.
 */
@RestController
@RequestMapping("/api")
public class ReportStatusResource {

    private final Logger log = LoggerFactory.getLogger(ReportStatusResource.class);

    private static final String ENTITY_NAME = "reportStatus";

    private final ReportStatusService reportStatusService;

    private final ReportStatusQueryService reportStatusQueryService;

    public ReportStatusResource(ReportStatusService reportStatusService, ReportStatusQueryService reportStatusQueryService) {
        this.reportStatusService = reportStatusService;
        this.reportStatusQueryService = reportStatusQueryService;
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
    public ResponseEntity<ReportStatus> createReportStatus(@Valid @RequestBody ReportStatus reportStatus) throws URISyntaxException {
        log.debug("REST request to save ReportStatus : {}", reportStatus);
        if (reportStatus.getId() != null) {
            throw new BadRequestAlertException("A new reportStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ReportStatus result = reportStatusService.save(reportStatus);
        return ResponseEntity.created(new URI("/api/report-statuses/" + result.getId()))
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
    public ResponseEntity<ReportStatus> updateReportStatus(@Valid @RequestBody ReportStatus reportStatus) throws URISyntaxException {
        log.debug("REST request to update ReportStatus : {}", reportStatus);
        if (reportStatus.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ReportStatus result = reportStatusService.save(reportStatus);
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
    @Timed
    public ResponseEntity<List<ReportStatus>> getAllReportStatuses(ReportStatusCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ReportStatuses by criteria: {}", criteria);
        Page<ReportStatus> page = reportStatusQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/report-statuses");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
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
     * GET  /report-statuses/:id : get the "id" reportStatus.
     *
     * @param id the id of the reportStatus to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the reportStatus, or with status 404 (Not Found)
     */
    @GetMapping("/report-statuses/{id}")
    @Timed
    public ResponseEntity<ReportStatus> getReportStatus(@PathVariable Long id) {
        log.debug("REST request to get ReportStatus : {}", id);
        Optional<ReportStatus> reportStatus = reportStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reportStatus);
    }

    /**
     * DELETE  /report-statuses/:id : delete the "id" reportStatus.
     *
     * @param id the id of the reportStatus to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/report-statuses/{id}")
    @Timed
    public ResponseEntity<Void> deleteReportStatus(@PathVariable Long id) {
        log.debug("REST request to delete ReportStatus : {}", id);
        reportStatusService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
