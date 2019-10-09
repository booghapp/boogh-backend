package org.boogh.service.dto;

import io.github.jhipster.service.filter.*;
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

    private ReportStateFilter state;

    private BooleanFilter anonymous;

    private DoubleFilter latitude;

    private DoubleFilter longitude;

    private LocalDateFilter date;

    private StringFilter title;

    private LongFilter commentsId;

    private LongFilter reporterId;

    private LongFilter reportStatusId;

    private LongFilter parentId;

    private LongFilter honkId;

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

    public DoubleFilter getLatitude() {
        return latitude;
    }

    public void setLatitude(DoubleFilter latitude) {
        this.latitude = latitude;
    }

    public DoubleFilter getLongitude() {
        return longitude;
    }

    public void setLongitude(DoubleFilter longitude) {
        this.longitude = longitude;
    }

    public LocalDateFilter getDate() {
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
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

    public LongFilter getHonkId() {
        return honkId;
    }

    public void setHonkId(LongFilter honkId) {
        this.honkId = honkId;
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
            Objects.equals(state, that.state) &&
            Objects.equals(anonymous, that.anonymous) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(date, that.date) &&
            Objects.equals(title, that.title) &&
            Objects.equals(commentsId, that.commentsId) &&
            Objects.equals(reporterId, that.reporterId) &&
            Objects.equals(reportStatusId, that.reportStatusId) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(honkId, that.honkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        type,
        state,
        anonymous,
        latitude,
        longitude,
        date,
        title,
        commentsId,
        reporterId,
        reportStatusId,
        parentId,
        honkId
        );
    }

    @Override
    public String toString() {
        return "ReportCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (state != null ? "state=" + state + ", " : "") +
                (anonymous != null ? "anonymous=" + anonymous + ", " : "") +
                (latitude != null ? "latitude=" + latitude + ", " : "") +
                (longitude != null ? "longitude=" + longitude + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (commentsId != null ? "commentsId=" + commentsId + ", " : "") +
                (reporterId != null ? "reporterId=" + reporterId + ", " : "") +
                (reportStatusId != null ? "reportStatusId=" + reportStatusId + ", " : "") +
                (parentId != null ? "parentId=" + parentId + ", " : "") +
                (honkId != null ? "honkId=" + honkId + ", " : "") +
            "}";
    }

}
