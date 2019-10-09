package org.boogh.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import org.boogh.domain.Comment;
import org.boogh.service.CommentQueryService;
import org.boogh.service.CommentService;
import org.boogh.service.dto.CommentCriteria;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.util.HeaderUtil;
import org.boogh.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Comment.
 */
@RestController
@RequestMapping("/api")
public class CommentResource {

    private final Logger log = LoggerFactory.getLogger(CommentResource.class);

    private static final String ENTITY_NAME = "comment";

    private final CommentService commentService;

    private final CommentQueryService commentQueryService;

    public CommentResource(CommentService commentService, CommentQueryService commentQueryService) {
        this.commentService = commentService;
        this.commentQueryService = commentQueryService;
    }

    /**
     * POST  /comments : Create a new comment.
     *
     * @param comment the comment to create
     * @return the ResponseEntity with status 201 (Created) and with body the new comment, or with status 400 (Bad Request) if the comment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/comments")
    public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) throws URISyntaxException {
        log.debug("REST request to save Comment : {}", comment);
        if (comment.getId() != null) {
            throw new BadRequestAlertException("A new comment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Comment result = commentService.save(comment);
        return ResponseEntity.created(new URI("/api/comments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /comments : Updates an existing comment.
     *
     * @param comment the comment to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated comment,
     * or with status 400 (Bad Request) if the comment is not valid,
     * or with status 500 (Internal Server Error) if the comment couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/comments")
    public ResponseEntity<Comment> updateComment(@Valid @RequestBody Comment comment) throws URISyntaxException {
        log.debug("REST request to update Comment : {}", comment);
        if (comment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Comment result = commentService.save(comment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, comment.getId().toString()))
            .body(result);
    }

    /**
     * GET  /comments : get all the comments.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of comments in body
     */
    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getAllComments(CommentCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Comments by criteria: {}", criteria);
        Page<Comment> page = commentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/comments");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /comments/count : count all the comments.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/comments/count")
    public ResponseEntity<Long> countComments(CommentCriteria criteria) {
        log.debug("REST request to count Comments by criteria: {}", criteria);
        return ResponseEntity.ok().body(commentQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /comments/:id : get the "id" comment.
     *
     * @param id the id of the comment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the comment, or with status 404 (Not Found)
     */
    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable Long id) {
        log.debug("REST request to get Comment : {}", id);
        Optional<Comment> comment = commentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(comment);
    }

    /**
     * DELETE  /comments/:id : delete the "id" comment.
     *
     * @param id the id of the comment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.debug("REST request to delete Comment : {}", id);
        commentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
