package org.boogh.service;

import io.github.jhipster.service.QueryService;
import org.boogh.domain.Reporter_;
import org.boogh.domain.User_;
import org.boogh.repository.ReporterRepository;
import org.boogh.service.dto.ReporterCriteria;
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
 * Service for executing complex queries for Reporter entities in the database.
 * The main input is a {@link ReporterCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link org.boogh.domain.Reporter} or a {@link Page} of {@link org.boogh.domain.Reporter} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReporterQueryService extends QueryService<org.boogh.domain.Reporter> {

    private final Logger log = LoggerFactory.getLogger(ReporterQueryService.class);

    private final ReporterRepository reporterRepository;

    public ReporterQueryService(ReporterRepository reporterRepository) {
        this.reporterRepository = reporterRepository;
    }

    /**
     * Return a {@link List} of {@link org.boogh.domain.Reporter} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<org.boogh.domain.Reporter> findByCriteria(ReporterCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<org.boogh.domain.Reporter> specification = createSpecification(criteria);
        return reporterRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link org.boogh.domain.Reporter} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<org.boogh.domain.Reporter> findByCriteria(ReporterCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<org.boogh.domain.Reporter> specification = createSpecification(criteria);
        return reporterRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReporterCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<org.boogh.domain.Reporter> specification = createSpecification(criteria);
        return reporterRepository.count(specification);
    }

    /**
     * Function to convert ReporterCriteria to a {@link Specification}
     */
    private Specification<org.boogh.domain.Reporter> createSpecification(ReporterCriteria criteria) {
        Specification<org.boogh.domain.Reporter> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Reporter_.id));
            }
            if (criteria.getKarma() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getKarma(), Reporter_.karma));
            }
            if (criteria.getVisibility() != null) {
                specification = specification.and(buildSpecification(criteria.getVisibility(), Reporter_.visibility));
            }
            if (criteria.getModerator() != null) {
                specification = specification.and(buildSpecification(criteria.getModerator(), Reporter_.moderator));
            }
            if (criteria.getLocation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLocation(), Reporter_.location));
            }
            if (criteria.getNotificationsOn() != null) {
                specification = specification.and(buildSpecification(criteria.getNotificationsOn(), Reporter_.notificationsOn));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(Reporter_.user, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
