package org.boogh.repository;

import org.boogh.domain.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the ReportStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReportStatusRepository extends JpaRepository<ReportStatus, Long>, JpaSpecificationExecutor<ReportStatus> {

    @Query("select report_status from ReportStatus report_status where report_status.reporter.login = ?#{principal.username}")
    List<ReportStatus> findByReporterIsCurrentUser();

    @Query("select report_status from ReportStatus report_status where report_status.reporter.id = ?1 and report_status.report.id = ?2")
    List<ReportStatus> findByReporterandReport(Long reporterId, Long id);

}
