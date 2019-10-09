package org.boogh.clientservice;

import io.github.jhipster.service.QueryService;
import org.boogh.domain.Comment_;
import org.boogh.domain.Report_;
import org.boogh.domain.User_;
import org.boogh.domain.Comment;
import org.boogh.repository.CommentRepository;
import org.boogh.service.dto.CommentCriteria;
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
 * Service for executing complex queries for Comment entities in the database.
 * The main input is a {@link CommentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Comment} or a {@link Page} of {@link Comment} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClientCommentQueryService extends QueryService<Comment> {

    private final Logger log = LoggerFactory.getLogger(ClientCommentQueryService.class);

    private final CommentRepository commentRepository;

    public ClientCommentQueryService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Return a {@link List} of {@link Comment} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Comment> findByCriteria(CommentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Comment> specification = createSpecification(criteria);
        return commentRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Comment} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Comment> findByCriteria(CommentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Comment> specification = createSpecification(criteria);
        return commentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Comment> specification = createSpecification(criteria);
        return commentRepository.count(specification);
    }

    /**
     * Function to convert CommentCriteria to a {@link Specification}
     */
    private Specification<Comment> createSpecification(CommentCriteria criteria) {
        Specification<Comment> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Comment_.id));
            }
            if (criteria.getCommenterId() != null) {
                specification = specification.and(buildSpecification(criteria.getCommenterId(),
                    root -> root.join(Comment_.commenter, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getReportId() != null) {
                specification = specification.and(buildSpecification(criteria.getReportId(),
                    root -> root.join(Comment_.report, JoinType.LEFT).get(Report_.id)));
            }
            if (criteria.getParentId() != null) {
                specification = specification.and(buildSpecification(criteria.getParentId(),
                    root -> root.join(Comment_.parent, JoinType.LEFT).get(Comment_.id)));
            }
        }
        return specification;
    }
}
