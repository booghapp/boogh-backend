package org.boogh.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vividsolutions.jts.geom.Point;
import org.boogh.domain.enumeration.ReportState;
import org.boogh.domain.enumeration.ReportType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A Report.
 */
@Entity
@Table(name = "report")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_type", nullable = false)
    private ReportType type;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @JsonIgnore
    @Column(name = "location")
    private Point location;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private ReportState state;

    @Column(name = "anonymous")
    private Boolean anonymous;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "date")
    private LocalDate date;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "report")
    //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("")
    private User reporter;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "report")
    @JsonIgnoreProperties("report")
    private Set<ReportStatus> reportStatuses = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("")
    private Report parent;

    @Transient
    boolean favoritedByCurrentUser = false;

    @Transient
    boolean isCurrentUsersReport = false;

    @Transient
    private List<String> images;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "report")
    //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Honk> honks = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportType getType() {
        return type;
    }

    public Report type(ReportType type) {
        this.type = type;
        return this;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public Report description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Point getLocation() {
        return location;
    }

    public Report location(Point location) {
        this.location = location;
        return this;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ReportState getState() {
        return state;
    }

    public Report state(ReportState state) {
        this.state = state;
        return this;
    }

    public void setState(ReportState state) {
        this.state = state;
    }

    public Boolean isAnonymous() {
        return anonymous;
    }

    public Report anonymous(Boolean anonymous) {
        this.anonymous = anonymous;
        return this;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Report latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Report longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Report comments(Set<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Report addComments(Comment comment) {
        this.comments.add(comment);
        comment.setReport(this);
        return this;
    }

    public Report removeComments(Comment comment) {
        this.comments.remove(comment);
        comment.setReport(null);
        return this;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public User getReporter() {
        return reporter;
    }

    public Report reporter(User user) {
        this.reporter = user;
        return this;
    }

    public void setReporter(User user) {
        this.reporter = user;
    }

    public Set<ReportStatus> getReportStatuses() {
        return reportStatuses;
    }

    public Report reportStatuses(Set<ReportStatus> reportStatuses) {
        this.reportStatuses = reportStatuses;
        return this;
    }

    public Report addReportStatus(ReportStatus reportStatus) {
        this.reportStatuses.add(reportStatus);
        reportStatus.setReport(this);
        return this;
    }

    public Report removeReportStatus(ReportStatus reportStatus) {
        this.reportStatuses.remove(reportStatus);
        reportStatus.setReport(null);
        return this;
    }

    public void setReportStatuses(Set<ReportStatus> reportStatuses) {
        this.reportStatuses = reportStatuses;
    }

    public Report getParent() {
        return parent;
    }

    public Report parent(Report report) {
        this.parent = report;
        return this;
    }

    public boolean isFavoritedByCurrentUser() {
        return favoritedByCurrentUser;
    }

    public void setFavoritedByCurrentUser(boolean favoritedByCurrentUser) {
        this.favoritedByCurrentUser = favoritedByCurrentUser;
    }

    public boolean isCurrentUsersReport() {
        return isCurrentUsersReport;
    }

    public void setCurrentUsersReport(boolean currentUsersReport) {
        isCurrentUsersReport = currentUsersReport;
    }

    public void setParent(Report report) {
        this.parent = report;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Set<Honk> getHonks() {
        return honks;
    }

    public Report honks(Set<Honk> honks) {
        this.honks = honks;
        return this;
    }

    public Report addHonk(Honk honk) {
        this.honks.add(honk);
        honk.setReport(this);
        return this;
    }

    public Report removeHonk(Honk honk) {
        this.honks.remove(honk);
        honk.setReport(null);
        return this;
    }

    public void setHonks(Set<Honk> honks) {
        this.honks = honks;
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
        Report report = (Report) o;
        if (report.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), report.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", description='" + getDescription() + "'" +
            ", location=" + getLocation() +
            ", state='" + getState() + "'" +
            ", anonymous='" + isAnonymous() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", date='" + getDate() + "'" +
            ", title='" + getTitle() + "'" +
            "}";
    }
}
