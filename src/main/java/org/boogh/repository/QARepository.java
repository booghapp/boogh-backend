package org.boogh.repository;

import org.boogh.domain.QA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the QA entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QARepository extends JpaRepository<QA, Long> {

    @Query("select qa from QA qa where qa.order = ?1")
    List<QA> findQAByOrder(Integer order);

    @Query("select max(qa.order) from QA qa")
    Integer findMaxOrderQA();

    @Query("select qa from QA qa order by qa.order asc")
    List<QA> findAllByOrderAsc();
}
