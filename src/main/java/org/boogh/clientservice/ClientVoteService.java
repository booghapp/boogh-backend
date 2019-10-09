package org.boogh.clientservice;

import org.boogh.domain.Vote;
import org.boogh.repository.VoteRepository;
import org.boogh.service.VoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Vote.
 */
@Service
@Transactional
public class ClientVoteService {

    private final Logger log = LoggerFactory.getLogger(VoteService.class);

    private final VoteRepository voteRepository;

    public ClientVoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    /**
     * Save a vote.
     *
     * @param vote the entity to save
     * @return the persisted entity
     */
    public Vote save(Vote vote) {
        log.debug("Request to save Vote : {}", vote);

        // Check whether or not the voting user has voted on this comment.

        List<Vote> votes = voteRepository.findByCommentAndVoter(vote.getComment().getId(), vote.getVoter().getId());
        if (!votes.isEmpty()) {
            Vote previousVote = votes.get(0);
            if (previousVote.getVote() == vote.getVote()) {
                previousVote.setVote(0);
            }else {
                previousVote.setVote(vote.getVote());
            }
            vote = previousVote;
        }
        return voteRepository.save(vote);
    }

    /**
     * Get all the votes.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Vote> findAll() {
        log.debug("Request to get all Votes");
        return voteRepository.findAll();
    }


    /**
     * Get one vote by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<Vote> findOne(Long id) {
        log.debug("Request to get Vote : {}", id);
        return voteRepository.findById(id);
    }

    /**
     * Delete the vote by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Vote : {}", id);
        voteRepository.deleteById(id);
    }

    /**
     * Get summation of votes for report.
     * @param commentId
     * @return
     */
    public Integer findTotalVotesForComment(Long commentId) {
        log.debug("Request to get summation of votes for Comment : {}", commentId);
        return voteRepository.findTotalVotesForComment(commentId);
    }
}
