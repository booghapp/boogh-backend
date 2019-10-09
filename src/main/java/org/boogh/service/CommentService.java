package org.boogh.service;

import org.boogh.domain.Comment;
import org.boogh.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Comment.
 */
@Service
@Transactional
public class CommentService {

    private final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Save a comment.
     *
     * @param comment the entity to save
     * @return the persisted entity
     */
    public Comment save(Comment comment) {
        log.debug("Request to save Comment : {}", comment);
        return commentRepository.save(comment);
    }

    /**
     * Get all the comments.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Comment> findAll(Pageable pageable) {
        log.debug("Request to get all Comments");
        return commentRepository.findAll(pageable);
    }


    /**
     * Get one comment by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<Comment> findOne(Long id) {
        log.debug("Request to get Comment : {}", id);
        return commentRepository.findById(id);
    }

    /**
     * Delete the comment by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Comment : {}", id);
        List<Comment> children = commentRepository.findAllChildReports(id);
        for(Comment comment: children){
            delete(comment.getId());
        }
        commentRepository.deleteById(id);
    }
}
