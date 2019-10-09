package org.boogh.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import org.boogh.domain.Vote;
import org.boogh.service.VoteQueryService;
import org.boogh.service.VoteService;
import org.boogh.service.dto.VoteCriteria;
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
 * REST controller for managing Vote.
 */
@RestController
@RequestMapping("/api")
public class VoteResource {

    private final Logger log = LoggerFactory.getLogger(VoteResource.class);

    private static final String ENTITY_NAME = "vote";

    private final VoteService voteService;

    private final VoteQueryService voteQueryService;

    public VoteResource(VoteService voteService, VoteQueryService voteQueryService) {
        this.voteService = voteService;
        this.voteQueryService = voteQueryService;
    }

    /**
     * POST  /votes : Create a new vote.
     *
     * @param vote the vote to create
     * @return the ResponseEntity with status 201 (Created) and with body the new vote, or with status 400 (Bad Request) if the vote has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/votes")
    public ResponseEntity<Vote> createVote(@Valid @RequestBody Vote vote) throws URISyntaxException {
        log.debug("REST request to save Vote : {}", vote);
        if (vote.getId() != null) {
            throw new BadRequestAlertException("A new vote cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Vote result = voteService.save(vote);
        return ResponseEntity.created(new URI("/api/votes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /votes : Updates an existing vote.
     *
     * @param vote the vote to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated vote,
     * or with status 400 (Bad Request) if the vote is not valid,
     * or with status 500 (Internal Server Error) if the vote couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/votes")
    public ResponseEntity<Vote> updateVote(@Valid @RequestBody Vote vote) throws URISyntaxException {
        log.debug("REST request to update Vote : {}", vote);
        if (vote.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Vote result = voteService.save(vote);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, vote.getId().toString()))
            .body(result);
    }

    /**
     * GET  /votes : get all the votes.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of votes in body
     */
    @GetMapping("/votes")
    public ResponseEntity<List<Vote>> getAllVotes(VoteCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Votes by criteria: {}", criteria);
        Page<Vote> page = voteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/votes");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /votes/count : count all the votes.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/votes/count")
    public ResponseEntity<Long> countVotes(VoteCriteria criteria) {
        log.debug("REST request to count Votes by criteria: {}", criteria);
        return ResponseEntity.ok().body(voteQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /votes/:id : get the "id" vote.
     *
     * @param id the id of the vote to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the vote, or with status 404 (Not Found)
     */
    @GetMapping("/votes/{id}")
    public ResponseEntity<Vote> getVote(@PathVariable Long id) {
        log.debug("REST request to get Vote : {}", id);
        Optional<Vote> vote = voteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vote);
    }

    /**
     * DELETE  /votes/:id : delete the "id" vote.
     *
     * @param id the id of the vote to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/votes/{id}")
    public ResponseEntity<Void> deleteVote(@PathVariable Long id) {
        log.debug("REST request to delete Vote : {}", id);
        voteService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
