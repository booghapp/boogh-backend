package org.boogh.service.dto;

import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the Vote entity. This class is used in VoteResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /votes?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class VoteCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter vote;

    private LongFilter voterId;

    private LongFilter commentId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getVote() {
        return vote;
    }

    public void setVote(IntegerFilter vote) {
        this.vote = vote;
    }

    public LongFilter getVoterId() {
        return voterId;
    }

    public void setVoterId(LongFilter voterId) {
        this.voterId = voterId;
    }

    public LongFilter getCommentId() {
        return commentId;
    }

    public void setCommentId(LongFilter commentId) {
        this.commentId = commentId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VoteCriteria that = (VoteCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(vote, that.vote) &&
            Objects.equals(voterId, that.voterId) &&
            Objects.equals(commentId, that.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        vote,
        voterId,
        commentId
        );
    }

    @Override
    public String toString() {
        return "VoteCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (vote != null ? "vote=" + vote + ", " : "") +
                (voterId != null ? "voterId=" + voterId + ", " : "") +
                (commentId != null ? "commentId=" + commentId + ", " : "") +
            "}";
    }

}
