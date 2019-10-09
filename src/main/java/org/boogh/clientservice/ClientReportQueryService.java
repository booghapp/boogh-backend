package org.boogh.clientservice;

import io.github.jhipster.service.QueryService;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.LongFilter;
import org.boogh.domain.Comment_;
import org.boogh.domain.ReportStatus_;
import org.boogh.domain.Report_;
import org.boogh.domain.User_;
import org.boogh.clientservice.dto.ReportCriteria;
import org.boogh.domain.enumeration.ReportState;
import org.boogh.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.util.Arrays;
import java.util.List;

/**
 * Service for executing complex queries for Report entities in the database.
 * The main input is a {@link ReportCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link org.boogh.domain.Report} or a {@link Page} of {@link org.boogh.domain.Report} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClientReportQueryService extends QueryService<org.boogh.domain.Report> {

    private final Logger log = LoggerFactory.getLogger(ClientReportQueryService.class);

    private final ReportRepository reportRepository;

    public ClientReportQueryService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Return a {@link List} of {@link org.boogh.domain.Report} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<org.boogh.domain.Report> findByCriteria(ReportCriteria criteria, Boolean hasAuthority) {
        log.debug("find by criteria : {}", criteria);
        final Specification<org.boogh.domain.Report> specification;
        LongFilter reporterId = criteria.getReporterId();

        //Default criteria
        ReportCriteria criteriaDefault = new ReportCriteria();
        ReportCriteria.ReportStateFilter filter = new ReportCriteria.ReportStateFilter();
        filter.setIn(Arrays.asList(ReportState.APPROVED));
        criteriaDefault.setState(filter);

        BooleanFilter anonymousFilter = new BooleanFilter();
        anonymousFilter.setIn(Arrays.asList(false));
        criteriaDefault.setAnonymous(anonymousFilter);

        if (hasAuthority){
            specification = createSpecification(criteria);

        }else if (reporterId != null){
            Long id = reporterId.getEquals();
            LongFilter longFilter = new LongFilter();
            longFilter.setEquals(id);
            criteriaDefault.setReporterId(longFilter);
            specification = createSpecification(criteriaDefault);

        }else{
            specification = createSpecification(criteriaDefault);
        }

        return reportRepository.findAll(specification);
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
        }
        return specification;
    }
}
