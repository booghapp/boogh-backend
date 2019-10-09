package org.boogh.clientservice.dto;

import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the ReportStatus entity. This class is used in ReportStatusResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /report-statuses?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ReportStatusCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private org.boogh.service.dto.ReportStatusCriteria.ReportStatusStateFilter saved;

    private org.boogh.service.dto.ReportStatusCriteria.ReportStatusStateFilter  flagged;

    private LongFilter reporterId;

    private LongFilter reportId;

    public ReportStatusCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public org.boogh.service.dto.ReportStatusCriteria.ReportStatusStateFilter  getSaved() {
        return saved;
    }

    public void setSaved(org.boogh.service.dto.ReportStatusCriteria.ReportStatusStateFilter  saved) {
        this.saved = saved;
    }

    public org.boogh.service.dto.ReportStatusCriteria.ReportStatusStateFilter getFlagged() {
        return flagged;
    }

    public void setFlagged(org.boogh.service.dto.ReportStatusCriteria.ReportStatusStateFilter  flagged) {
        this.flagged = flagged;
    }

    public LongFilter getReporterId() {
        return reporterId;
    }

    public void setReporterId(LongFilter reporterId) {
        this.reporterId = reporterId;
    }

    public LongFilter getReportId() {
        return reportId;
    }

    public void setReportId(LongFilter reportId) {
        this.reportId = reportId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReportStatusCriteria that = (ReportStatusCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(saved, that.saved) &&
            Objects.equals(flagged, that.flagged) &&
            Objects.equals(reporterId, that.reporterId) &&
            Objects.equals(reportId, that.reportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        saved,
        flagged,
        reporterId,
        reportId
        );
    }

    @Override
    public String toString() {
        return "ReportStatusCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (saved != null ? "saved=" + saved + ", " : "") +
                (flagged != null ? "flagged=" + flagged + ", " : "") +
                (reporterId != null ? "reporterId=" + reporterId + ", " : "") +
                (reportId != null ? "reportId=" + reportId + ", " : "") +
            "}";
    }

}
