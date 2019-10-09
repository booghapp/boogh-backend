package org.boogh.clientapi;

import org.boogh.clientservice.*;
import org.boogh.clientservice.dto.CommentDTO;
import org.boogh.clientservice.mapper.CommentMapper;
import org.boogh.config.Constants;
import org.boogh.domain.Comment;
import org.boogh.service.dto.CommentCriteria;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.errors.EmptyCommentException;
import org.boogh.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Comment.
 */
@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientCommentResource {

    private final Logger log = LoggerFactory.getLogger(ClientCommentResource.class);

    private static final String ENTITY_NAME = "comment";

    private final ClientCommentService clientCommentService;

    private final ClientCommentQueryService clientCommentQueryService;

    private final CheckAuth checkAuth;

    public ClientCommentResource(ClientCommentService clientCommentService, ClientCommentQueryService clientCommentQueryService, CheckAuth checkAuth) {
        this.clientCommentService = clientCommentService;
        this.clientCommentQueryService = clientCommentQueryService;
        this.checkAuth = checkAuth;
    }

    /**
     * POST  /comments : Create a new comment.
     *
     * @param comment the comment to create
     * @return the ResponseEntity with status 201 (Created) and with body the new comment, or with status 400 (Bad Request) if the comment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody Comment comment) throws URISyntaxException {
        log.debug("REST request to save Comment : {}", comment);
        if (comment.getId() != null) {
            throw new BadRequestAlertException("A new comment cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (!checkAuth.hasAuthority(comment.getCommenter().getId())) {
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }

        if (comment.getContent().length() == 0) {
            throw new EmptyCommentException();
        }

        if (comment.getParent() != null) {
            Optional<Comment> parent = clientCommentService.findOne(comment.getParent().getId());
            if (parent.equals(Optional.empty())) {
                throw new BadRequestAlertException("Parent comment does not exist", ENTITY_NAME, "parent does not exist");
            }
        }

        //Sanitize comment content
        HtmlEscaper.escapeComment(comment);
        comment.setDate(LocalDate.now());

        Comment result = clientCommentService.save(comment);
        CommentDTO commentDTO = new CommentDTO(result);
        return ResponseEntity.created(new URI("/api/comments/" + commentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, commentDTO.getId().toString()))
            .body(commentDTO);
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
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    public ResponseEntity<Comment> updateComment(@Valid @RequestBody Comment comment) throws URISyntaxException {
        log.debug("REST request to update Comment : {}", comment);
        if (comment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Comment result = clientCommentService.save(comment);
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
    public ResponseEntity<List<CommentNode>> getAllComments(CommentCriteria criteria) {
        log.debug("REST request to get Comments by criteria: {}", criteria);
        List<Comment> entityList = clientCommentQueryService.findByCriteria(criteria);

        // Convert to DTOs
        CommentMapper commentMapper = new CommentMapper();
        List<CommentDTO> commentDTOS = commentMapper.commentsToCommentDTOs(entityList);

        // Create comment tree
        CommentTree commentTree = new CommentTree(commentDTOS);
        commentTree.createCommentTree(commentTree.getHead());

        return ResponseEntity.ok().body(commentTree.getHead().getChildren());
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
        return ResponseEntity.ok().body(clientCommentQueryService.countByCriteria(criteria));
    }

    /**
     * DELETE  /comments/:id : delete the "id" comment.
     *
     * @param id the id of the comment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.debug("REST request to delete Comment : {}", id);
        clientCommentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
