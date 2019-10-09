package org.boogh.service;

import org.boogh.domain.Reporter;
import org.boogh.repository.ReporterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing Reporter.
 */
@Service
@Transactional
public class ReporterService {

    private final Logger log = LoggerFactory.getLogger(ReporterService.class);

    private final ReporterRepository reporterRepository;

    public ReporterService(ReporterRepository reporterRepository) {
        this.reporterRepository = reporterRepository;
    }

    /**
     * Save a reporter.
     *
     * @param reporter the entity to save
     * @return the persisted entity
     */
    public Reporter save(Reporter reporter) {
        log.debug("Request to save Reporter : {}", reporter);
        return reporterRepository.save(reporter);
    }

    /**
     * Get all the reporters.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Reporter> findAll(Pageable pageable) {
        log.debug("Request to get all Reporters");
        return reporterRepository.findAll(pageable);
    }


    /**
     * Get one reporter by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<Reporter> findOne(Long id) {
        log.debug("Request to get Reporter : {}", id);
        return reporterRepository.findById(id);
    }

    /**
     * Delete the reporter by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Reporter : {}", id);
        reporterRepository.deleteById(id);
    }
}
