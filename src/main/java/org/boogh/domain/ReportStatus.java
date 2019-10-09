package org.boogh.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.boogh.domain.enumeration.ReportStatusState;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ReportStatus.
 */
@Entity
@Table(name = "report_status")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ReportStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "saved")
    private ReportStatusState saved;

    @Enumerated(EnumType.STRING)
    @Column(name = "flagged")
    private ReportStatusState flagged;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("")
    private User reporter;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("")
    private Report report;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportStatusState getSaved() {
        return saved;
    }

    public ReportStatus saved(ReportStatusState saved) {
        this.saved = saved;
        return this;
    }

    public void setSaved(ReportStatusState saved) {
        this.saved = saved;
    }

    public ReportStatusState getFlagged() {
        return flagged;
    }

    public ReportStatus flagged(ReportStatusState flagged) {
        this.flagged = flagged;
        return this;
    }

    public void setFlagged(ReportStatusState flagged) {
        this.flagged = flagged;
    }

    public User getReporter() {
        return reporter;
    }

    public ReportStatus reporter(User user) {
        this.reporter = user;
        return this;
    }

    public void setReporter(User user) {
        this.reporter = user;
    }

    public Report getReport() {
        return report;
    }

    public ReportStatus report(Report report) {
        this.report = report;
        return this;
    }

    public void setReport(Report report) {
        this.report = report;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReportStatus reportStatus = (ReportStatus) o;
        if (reportStatus.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), reportStatus.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ReportStatus{" +
            "id=" + getId() +
            ", saved='" + getSaved() + "'" +
            ", flagged='" + getFlagged() + "'" +
            "}";
    }
}
