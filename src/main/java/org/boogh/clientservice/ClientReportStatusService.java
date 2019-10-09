package org.boogh.clientservice;

import org.boogh.domain.ReportStatus;
import org.boogh.domain.enumeration.ReportStatusState;
import org.boogh.repository.ReportStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing ReportStatus.
 */
@Service
@Transactional
public class ClientReportStatusService {

    private final Logger log = LoggerFactory.getLogger(ClientReportStatusService.class);

    private final ReportStatusRepository reportStatusRepository;

    public ClientReportStatusService(ReportStatusRepository reportStatusRepository) {
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

        List<ReportStatus> reportStatuses = reportStatusRepository.findByReporterandReport(reportStatus.getReporter().getId(),
            reportStatus.getReport().getId());

        if(!reportStatuses.isEmpty()){
            //There should only be at maximum one record for each combination of user.login + report.id
            ReportStatus reportStatus1 = reportStatuses.get(0);

            if(reportStatus.getSaved() != ReportStatusState.UNSET){
                reportStatus1.setSaved(reportStatus.getSaved());
            }

            if(reportStatus.getFlagged() != ReportStatusState.UNSET){
                reportStatus1.setFlagged(reportStatus.getFlagged());
            }

            reportStatus = reportStatus1;
        }

        return reportStatusRepository.save(reportStatus);
    }

    /**
     * Get all the reportStatuses.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ReportStatus> findAll() {
        log.debug("Request to get all ReportStatuses");
        return reportStatusRepository.findAll();
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
