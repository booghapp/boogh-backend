package org.boogh.clientapi;

import com.codahale.metrics.annotation.Timed;
import org.boogh.clientservice.CheckAuth;
import org.boogh.clientservice.ClientVoteQueryService;
import org.boogh.clientservice.ClientVoteService;
import org.boogh.clientservice.dto.VoteDTO;
import org.boogh.clientservice.mapper.VoteMapper;
import org.boogh.config.Constants;
import org.boogh.domain.Vote;
import org.boogh.service.dto.VoteCriteria;
import org.boogh.web.rest.VoteResource;
import org.boogh.web.rest.errors.BadRequestAlertException;
import org.boogh.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing Vote.
 */
@RestController
@RequestMapping(Constants.API_VERSION)
public class ClientVoteResource {

    private final Logger log = LoggerFactory.getLogger(VoteResource.class);

    private static final String ENTITY_NAME = "vote";

    private final ClientVoteService clientVoteService;

    private final ClientVoteQueryService clientVoteQueryService;

    private final CheckAuth checkAuth;

    public ClientVoteResource(ClientVoteService clientVoteService, ClientVoteQueryService clientVoteQueryService, CheckAuth checkAuth) {
        this.clientVoteService = clientVoteService;
        this.clientVoteQueryService = clientVoteQueryService;
        this.checkAuth = checkAuth;
    }

    /**
     * POST  /votes : Create a new vote.
     *
     * @param vote the vote to create
     * @return the ResponseEntity with status 201 (Created) and with body the new vote, or with status 400 (Bad Request) if the vote has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/votes")
    @Timed
    public ResponseEntity<VoteDTO> createVote(@Valid @RequestBody Vote vote) throws URISyntaxException {
        log.debug("REST request to save Vote : {}", vote);
        if (vote.getId() != null) {
            throw new BadRequestAlertException("A new vote cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (!checkAuth.hasAuthority(vote.getVoter().getId())) {
            throw new BadRequestAlertException("Not Authorized", ENTITY_NAME, "Invalid token");
        }

        if (vote.getVote() >= 1) {
            vote.setVote(1);
        } else if (vote.getVote() <= 1) {
            vote.setVote(-1);
        } else {
            vote.setVote(0);
        }

        Vote result = clientVoteService.save(vote);
        VoteDTO voteDTO = new VoteDTO(result);
        return ResponseEntity.created(new URI("/api/votes/" + voteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, voteDTO.getId().toString()))
            .body(voteDTO);
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
    @PreAuthorize("hasAuthority('ROLE_MODERATOR')")
    @Timed
    public ResponseEntity<Vote> updateVote(@Valid @RequestBody Vote vote) throws URISyntaxException {
        log.debug("REST request to update Vote : {}", vote);
        if (vote.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Vote result = clientVoteService.save(vote);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, vote.getId().toString()))
            .body(result);
    }

    /**
     * GET votes for a comment.
     * @param commentId the id of the comment whose votes will be summed.
     */
    @GetMapping(value = "/votes", params = {"commentId"})
    @Timed
    public ResponseEntity<Integer> getCommentVotes(@RequestParam Long commentId) {
        log.debug("REST request to get sum of votes");
        Integer summation = clientVoteService.findTotalVotesForComment(commentId);
        return ResponseEntity.ok(summation);
    }

    /**
     * GET  /votes : get all the votes.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of votes in body
     */
    @GetMapping("/votes")
    public ResponseEntity<List<VoteDTO>> getAllVotes(VoteCriteria criteria) {
        log.debug("REST request to get Votes by criteria: {}", criteria);
        List<Vote> entityList = clientVoteQueryService.findByCriteria(criteria);
        VoteMapper voteMapper = new VoteMapper();
        List<VoteDTO> votes = voteMapper.votesToVoteDTOs(entityList);
        return ResponseEntity.ok().body(votes);
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
        return ResponseEntity.ok().body(clientVoteQueryService.countByCriteria(criteria));
    }
}
