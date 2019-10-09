package org.boogh.clientservice.dto;

import io.github.jhipster.service.filter.BigDecimalFilter;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import org.boogh.domain.enumeration.ReportState;
import org.boogh.domain.enumeration.ReportType;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the Report entity. This class is used in ReportResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /reports?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ReportCriteria implements Serializable {
    /**
     * Class for filtering ReportType
     */
    public static class ReportTypeFilter extends Filter<ReportType> {
    }
    /**
     * Class for filtering ReportState
     */
    public static class ReportStateFilter extends Filter<ReportState> {
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ReportTypeFilter type;

    private BigDecimalFilter location;

    private ReportStateFilter state;

    private BooleanFilter anonymous;

    private LongFilter commentsId;

    private LongFilter reporterId;

    private LongFilter reportStatusId;

    private LongFilter parentId;

    public ReportCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ReportTypeFilter getType() {
        return type;
    }

    public void setType(ReportTypeFilter type) {
        this.type = type;
    }

    public BigDecimalFilter getLocation() {
        return location;
    }

    public void setLocation(BigDecimalFilter location) {
        this.location = location;
    }

    public ReportStateFilter getState() {
        return state;
    }

    public void setState(ReportStateFilter state) {
        this.state = state;
    }

    public BooleanFilter getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(BooleanFilter anonymous) {
        this.anonymous = anonymous;
    }

    public LongFilter getCommentsId() {
        return commentsId;
    }

    public void setCommentsId(LongFilter commentsId) {
        this.commentsId = commentsId;
    }

    public LongFilter getReporterId() {
        return reporterId;
    }

    public void setReporterId(LongFilter reporterId) {
        this.reporterId = reporterId;
    }

    public LongFilter getReportStatusId() {
        return reportStatusId;
    }

    public void setReportStatusId(LongFilter reportStatusId) {
        this.reportStatusId = reportStatusId;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReportCriteria that = (ReportCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(location, that.location) &&
            Objects.equals(state, that.state) &&
            Objects.equals(anonymous, that.anonymous) &&
            Objects.equals(commentsId, that.commentsId) &&
            Objects.equals(reporterId, that.reporterId) &&
            Objects.equals(reportStatusId, that.reportStatusId) &&
            Objects.equals(parentId, that.parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        type,
        location,
        state,
        anonymous,
        commentsId,
        reporterId,
        reportStatusId,
        parentId
        );
    }

    @Override
    public String toString() {
        return "ReportCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (location != null ? "location=" + location + ", " : "") +
                (state != null ? "state=" + state + ", " : "") +
                (anonymous != null ? "anonymous=" + anonymous + ", " : "") +
                (commentsId != null ? "commentsId=" + commentsId + ", " : "") +
                (reporterId != null ? "reporterId=" + reporterId + ", " : "") +
                (reportStatusId != null ? "reportStatusId=" + reportStatusId + ", " : "") +
                (parentId != null ? "parentId=" + parentId + ", " : "") +
            "}";
    }

}
