package org.boogh.clientservice;

import io.github.jhipster.service.QueryService;
import org.boogh.domain.Comment_;
import org.boogh.domain.User_;
import org.boogh.domain.Vote_;
import org.boogh.repository.VoteRepository;
import org.boogh.service.VoteQueryService;
import org.boogh.service.dto.VoteCriteria;
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
 * Service for executing complex queries for Vote entities in the database.
 * The main input is a {@link VoteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link org.boogh.domain.Vote} or a {@link Page} of {@link org.boogh.domain.Vote} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClientVoteQueryService extends QueryService<org.boogh.domain.Vote> {

    private final Logger log = LoggerFactory.getLogger(VoteQueryService.class);

    private final VoteRepository voteRepository;

    public ClientVoteQueryService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    /**
     * Return a {@link List} of {@link org.boogh.domain.Vote} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<org.boogh.domain.Vote> findByCriteria(VoteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<org.boogh.domain.Vote> specification = createSpecification(criteria);
        return voteRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link org.boogh.domain.Vote} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<org.boogh.domain.Vote> findByCriteria(VoteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<org.boogh.domain.Vote> specification = createSpecification(criteria);
        return voteRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VoteCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<org.boogh.domain.Vote> specification = createSpecification(criteria);
        return voteRepository.count(specification);
    }

    /**
     * Function to convert VoteCriteria to a {@link Specification}
     */
    private Specification<org.boogh.domain.Vote> createSpecification(VoteCriteria criteria) {
        Specification<org.boogh.domain.Vote> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Vote_.id));
            }
            if (criteria.getVote() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVote(), Vote_.vote));
            }
            if (criteria.getVoterId() != null) {
                specification = specification.and(buildSpecification(criteria.getVoterId(),
                    root -> root.join(Vote_.voter, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getCommentId() != null) {
                specification = specification.and(buildSpecification(criteria.getCommentId(),
                    root -> root.join(Vote_.comment, JoinType.LEFT).get(Comment_.id)));
            }
        }
        return specification;
    }
}
