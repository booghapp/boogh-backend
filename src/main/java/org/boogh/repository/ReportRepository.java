package org.boogh.repository;

import com.vividsolutions.jts.geom.Geometry;
import org.boogh.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Report entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    @Query("select report from Report report where report.reporter.login = ?#{principal.username}")
    List<Report> findByReporterIsCurrentUser();

    @Query("select r from Report r where within(r.location, ?1) = true and r.state = 'APPROVED' and r.parent = null")
    List<Report> findWithinGeometry(Geometry geometry);

    @Query("select r from Report r where r.state = 'APPROVED' and r.parent = null order by r.id DESC" )
    List<Report> findAllOrderByIdDesc();

    @Query("select r from Report r where r.parent.id = ?1")
    List<Report> findAllChildReports(long id);

    @Query("select r from Report r where r.parent.id = ?1 and r.state = 'APPROVED'")
    List<Report> findApprovedChildReports(long id);
}
