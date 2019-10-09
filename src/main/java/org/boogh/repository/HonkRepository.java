package org.boogh.repository;

import org.boogh.domain.Honk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Honk entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HonkRepository extends JpaRepository<Honk, Long> {

    @Query("select honk from Honk honk where honk.user.login = ?#{principal.username}")
    List<Honk> findByUserIsCurrentUser();

    @Query("select honk from Honk honk where honk.user.id = ?1 and honk.report.id = ?2")
    List<Honk> findUserHonkForReport(Long userId, Long reportId);

    @Query("select count(honk) from Honk honk where honk.report.id = ?1 and honk.honked = true")
    Integer findNumHonksForReport(Long reportId);
}
