package org.boogh.service;

import io.github.jhipster.service.QueryService;
import org.boogh.domain.ReportStatus_;
import org.boogh.domain.Report_;
import org.boogh.domain.User_;
import org.boogh.repository.ReportStatusRepository;
import org.boogh.service.dto.ReportStatusCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.util.List;


/**
 * Service for executing complex queries for ReportStatus entities in the database.
 * The main input is a {@link ReportStatusCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link org.boogh.domain.ReportStatus} or a {@link Page} of {@link org.boogh.domain.ReportStatus} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReportStatusQueryService extends QueryService<org.boogh.domain.ReportStatus> {

    private final Logger log = LoggerFactory.getLogger(ReportStatusQueryService.class);

    private final ReportStatusRepository reportStatusRepository;

    public ReportStatusQueryService(ReportStatusRepository reportStatusRepository) {
        this.reportStatusRepository = reportStatusRepository;
    }

    /**
     * Return a {@link List} of {@link org.boogh.domain.ReportStatus} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<org.boogh.domain.ReportStatus> findByCriteria(ReportStatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<org.boogh.domain.ReportStatus> specification = createSpecification(criteria);
        return reportStatusRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link org.boogh.domain.ReportStatus} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<org.boogh.domain.ReportStatus> findByCriteria(ReportStatusCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<org.boogh.domain.ReportStatus> specification = createSpecification(criteria);
        return reportStatusRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReportStatusCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<org.boogh.domain.ReportStatus> specification = createSpecification(criteria);
        return reportStatusRepository.count(specification);
    }

    /**
     * Function to convert ReportStatusCriteria to a {@link Specification}
     */
    private Specification<org.boogh.domain.ReportStatus> createSpecification(ReportStatusCriteria criteria) {
        Specification<org.boogh.domain.ReportStatus> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ReportStatus_.id));
            }
            if (criteria.getSaved() != null) {
                specification = specification.and(buildSpecification(criteria.getSaved(), ReportStatus_.saved));
            }
            if (criteria.getFlagged() != null) {
                specification = specification.and(buildSpecification(criteria.getFlagged(), ReportStatus_.flagged));
            }
            if (criteria.getReporterId() != null) {
                specification = specification.and(buildSpecification(criteria.getReporterId(),
                    root -> root.join(ReportStatus_.reporter, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getReportId() != null) {
                specification = specification.and(buildSpecification(criteria.getReportId(),
                    root -> root.join(ReportStatus_.report, JoinType.LEFT).get(Report_.id)));
            }
        }
        return specification;
    }
}
