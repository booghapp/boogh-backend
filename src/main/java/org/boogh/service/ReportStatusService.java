package org.boogh.service;

import org.boogh.domain.ReportStatus;
import org.boogh.repository.ReportStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing ReportStatus.
 */
@Service
@Transactional
public class ReportStatusService {

    private final Logger log = LoggerFactory.getLogger(ReportStatusService.class);

    private final ReportStatusRepository reportStatusRepository;

    public ReportStatusService(ReportStatusRepository reportStatusRepository) {
        this.reportStatusRepository = reportStatusRepository;
    }

    /**
     * Save a reportStatus.
     *
     * @param reportStatus the entity to save
     * @return the persisted entity
     */
    public ReportStatus save(ReportStatus reportStatus) {
        log.debug("Request to save ReportStatus : {}", reportStatus);
        return reportStatusRepository.save(reportStatus);
    }

    /**
     * Get all the reportStatuses.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ReportStatus> findAll(Pageable pageable)  {
        log.debug("Request to get all ReportStatuses");
        return reportStatusRepository.findAll(pageable);
    }


    /**
     * Get one reportStatus by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<ReportStatus> findOne(Long id) {
        log.debug("Request to get ReportStatus : {}", id);
        return reportStatusRepository.findById(id);
    }

    /**
     * Delete the reportStatus by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ReportStatus : {}", id);
        reportStatusRepository.deleteById(id);
    }
}
