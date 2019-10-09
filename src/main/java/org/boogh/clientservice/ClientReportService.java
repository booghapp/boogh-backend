package org.boogh.clientservice;

import com.vividsolutions.jts.geom.Geometry;
import org.boogh.domain.Report;
import org.boogh.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Report.
 */
@Service
@Transactional
public class ClientReportService {

    private final Logger log = LoggerFactory.getLogger(ClientReportService.class);

    private final ReportRepository reportRepository;

    public ClientReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Save a report.
     *
     * @param report the entity to save
     * @return the persisted entity
     */
    public Report save(Report report) {
        log.debug("Request to save Report : {}", report);
        return reportRepository.save(report);
    }

    /**
     * Get all the reports.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Report> findAll() {
        log.debug("Request to get all Reports");
        return reportRepository.findAll();
    }


    /**
     * Get one report by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<Report> findOne(Long id) {
        log.debug("Request to get Report : {}", id);
        return reportRepository.findById(id);
    }

    /**
     * Get report and it's updates by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public List<Report> findReport(Long id, Boolean hasAuthority) {
        log.debug("Request to get Report : {}", id);
        List<Report> entityList = new ArrayList<>();
        Optional<Report> orginalReport = reportRepository.findById(id);
        entityList.add(orginalReport.get());
        if (hasAuthority) {
            List<Report> updates = reportRepository.findAllChildReports(id);
            entityList.addAll(updates);
        } else {
            List<Report> updates = reportRepository.findApprovedChildReports(id);
            entityList.addAll(updates);
        }

        return entityList;
    }

    /**
     * Delete the report by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Report : {}", id);
        List<Report> children = reportRepository.findAllChildReports(id);
        for(Report report: children){
            reportRepository.deleteById(report.getId());
        }
        reportRepository.deleteById(id);
    }

    /**
     * Get all the reports within bounding box.
     *
     * @return the list of entities
     */
    public List<Report> findWithinGeometry(Geometry boundingBox) {
        return reportRepository.findWithinGeometry(boundingBox);
    }

    /**
     * Get all the reports ordered by id desc.
     *
     * @return the list of entities
     */
    public List<Report> findByIdDesc(){
        return reportRepository.findAllOrderByIdDesc();
    }
}
