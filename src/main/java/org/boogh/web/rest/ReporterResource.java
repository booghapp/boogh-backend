package org.boogh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.boogh.domain.Reporter;
import org.boogh.service.ReporterQueryService;
import org.boogh.service.ReporterService;
import org.boogh.service.dto.ReporterCriteria;
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
 * REST controller for managing Reporter.
 */
@RestController
@RequestMapping("/api")
public class ReporterResource {

    private final Logger log = LoggerFactory.getLogger(ReporterResource.class);

    private static final String ENTITY_NAME = "reporter";

    private final ReporterService reporterService;

    private final ReporterQueryService reporterQueryService;

    public ReporterResource(ReporterService reporterService, ReporterQueryService reporterQueryService) {
        this.reporterService = reporterService;
        this.reporterQueryService = reporterQueryService;
    }

    /**
     * POST  /reporters : Create a new reporter.
     *
     * @param reporter the reporter to create
     * @return the ResponseEntity with status 201 (Created) and with body the new reporter, or with status 400 (Bad Request) if the reporter has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/reporters")
    @Timed
    public ResponseEntity<Reporter> createReporter(@Valid @RequestBody Reporter reporter) throws URISyntaxException {
        log.debug("REST request to save Reporter : {}", reporter);
        if (reporter.getId() != null) {
            throw new BadRequestAlertException("A new reporter cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Reporter result = reporterService.save(reporter);
        return ResponseEntity.created(new URI("/api/reporters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reporters : Updates an existing reporter.
     *
     * @param reporter the reporter to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated reporter,
     * or with status 400 (Bad Request) if the reporter is not valid,
     * or with status 500 (Internal Server Error) if the reporter couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/reporters")
    @Timed
    public ResponseEntity<Reporter> updateReporter(@Valid @RequestBody Reporter reporter) throws URISyntaxException {
        log.debug("REST request to update Reporter : {}", reporter);
        if (reporter.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Reporter result = reporterService.save(reporter);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, reporter.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reporters : get all the reporters.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of reporters in body
     */
    @GetMapping("/reporters")
    @Timed
    public ResponseEntity<List<Reporter>> getAllReporters(ReporterCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Reporters by criteria: {}", criteria);
        Page<Reporter> page = reporterQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reporters");
        return ResponseEntity.ok().headers(headers).body(page.getContent());

    }

    /**
    * GET  /reporters/count : count all the reporters.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/reporters/count")
    @Timed
    public ResponseEntity<Long> countReporters(ReporterCriteria criteria) {
        log.debug("REST request to count Reporters by criteria: {}", criteria);
        return ResponseEntity.ok().body(reporterQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /reporters/:id : get the "id" reporter.
     *
     * @param id the id of the reporter to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the reporter, or with status 404 (Not Found)
     */
    @GetMapping("/reporters/{id}")
    @Timed
    public ResponseEntity<Reporter> getReporter(@PathVariable Long id) {
        log.debug("REST request to get Reporter : {}", id);
        Optional<Reporter> reporter = reporterService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reporter);
    }

    /**
     * DELETE  /reporters/:id : delete the "id" reporter.
     *
     * @param id the id of the reporter to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/reporters/{id}")
    @Timed
    public ResponseEntity<Void> deleteReporter(@PathVariable Long id) {
        log.debug("REST request to delete Reporter : {}", id);
        reporterService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
