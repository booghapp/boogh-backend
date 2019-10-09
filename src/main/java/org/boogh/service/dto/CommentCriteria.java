package org.boogh.service.dto;

import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LocalDateFilter;
import io.github.jhipster.service.filter.LongFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the Comment entity. This class is used in CommentResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /comments?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CommentCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter date;

    private LongFilter commenterId;

    private LongFilter reportId;

    private LongFilter parentId;

    private LongFilter voteId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getDate() {
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public LongFilter getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(LongFilter commenterId) {
        this.commenterId = commenterId;
    }

    public LongFilter getReportId() {
        return reportId;
    }

    public void setReportId(LongFilter reportId) {
        this.reportId = reportId;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public LongFilter getVoteId() {
        return voteId;
    }

    public void setVoteId(LongFilter voteId) {
        this.voteId = voteId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CommentCriteria that = (CommentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(commenterId, that.commenterId) &&
            Objects.equals(reportId, that.reportId) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(voteId, that.voteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        date,
        commenterId,
        reportId,
        parentId,
        voteId
        );
    }

    @Override
    public String toString() {
        return "CommentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (commenterId != null ? "commenterId=" + commenterId + ", " : "") +
                (reportId != null ? "reportId=" + reportId + ", " : "") +
                (parentId != null ? "parentId=" + parentId + ", " : "") +
                (voteId != null ? "voteId=" + voteId + ", " : "") +
            "}";
    }

}
