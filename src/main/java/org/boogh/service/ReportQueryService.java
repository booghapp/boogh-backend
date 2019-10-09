package org.boogh.service;

import io.github.jhipster.service.QueryService;
import org.boogh.domain.*;
import org.boogh.repository.ReportRepository;
import org.boogh.service.dto.ReportCriteria;
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
 * Service for executing complex queries for Report entities in the database.
 * The main input is a {@link ReportCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link org.boogh.domain.Report} or a {@link Page} of {@link org.boogh.domain.Report} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReportQueryService extends QueryService<org.boogh.domain.Report> {

    private final Logger log = LoggerFactory.getLogger(ReportQueryService.class);

    private final ReportRepository reportRepository;

    public ReportQueryService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Return a {@link List} of {@link org.boogh.domain.Report} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<org.boogh.domain.Report> findByCriteria(ReportCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<org.boogh.domain.Report> specification = createSpecification(criteria);
        return reportRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link org.boogh.domain.Report} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<org.boogh.domain.Report> findByCriteria(ReportCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<org.boogh.domain.Report> specification = createSpecification(criteria);
        return reportRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReportCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<org.boogh.domain.Report> specification = createSpecification(criteria);
        return reportRepository.count(specification);
    }

    /**
     * Function to convert ReportCriteria to a {@link Specification}
     */
    private Specification<org.boogh.domain.Report> createSpecification(ReportCriteria criteria) {
        Specification<org.boogh.domain.Report> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Report_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Report_.type));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildSpecification(criteria.getState(), Report_.state));
            }
            if (criteria.getAnonymous() != null) {
                specification = specification.and(buildSpecification(criteria.getAnonymous(), Report_.anonymous));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLatitude(), Report_.latitude));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLongitude(), Report_.longitude));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Report_.date));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Report_.title));
            }
            if (criteria.getCommentsId() != null) {
                specification = specification.and(buildSpecification(criteria.getCommentsId(),
                    root -> root.join(Report_.comments, JoinType.LEFT).get(Comment_.id)));
            }
            if (criteria.getReporterId() != null) {
                specification = specification.and(buildSpecification(criteria.getReporterId(),
                    root -> root.join(Report_.reporter, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getReportStatusId() != null) {
                specification = specification.and(buildSpecification(criteria.getReportStatusId(),
                    root -> root.join(Report_.reportStatuses, JoinType.LEFT).get(ReportStatus_.id)));
            }
            if (criteria.getParentId() != null) {
                specification = specification.and(buildSpecification(criteria.getParentId(),
                    root -> root.join(Report_.parent, JoinType.LEFT).get(Report_.id)));
            }
            if (criteria.getHonkId() != null) {
                specification = specification.and(buildSpecification(criteria.getHonkId(),
                    root -> root.join(Report_.honks, JoinType.LEFT).get(Honk_.id)));
            }
        }
        return specification;
    }
}
