package org.boogh.repository;

import org.boogh.domain.Reporter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Reporter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReporterRepository extends JpaRepository<Reporter, Long>, JpaSpecificationExecutor<Reporter> {

    Optional<Reporter> findByUserId(Long userId);
}
