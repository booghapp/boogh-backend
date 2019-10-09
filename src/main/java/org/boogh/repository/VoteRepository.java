package org.boogh.repository;

import org.boogh.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Vote entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>, JpaSpecificationExecutor<Vote> {

    @Query("select vote from Vote vote where vote.voter.login = ?#{principal.username}")
    List<Vote> findByVoterIsCurrentUser();

    @Query("select sum(vote.vote) from Vote vote where vote.comment.id = ?1")
    Integer findTotalVotesForComment(long id);

    @Query("select vote from Vote vote where vote.comment.id = ?1 and vote.voter.id = ?2")
    List<Vote> findByCommentAndVoter(long commentId, long voterId);
}
