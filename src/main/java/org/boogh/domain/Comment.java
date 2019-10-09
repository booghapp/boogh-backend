package org.boogh.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Comment.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "jhi_date")
    private LocalDate date;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("comments")
    private User commenter;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("comments")
    private Report report;

    @ManyToOne
    @JsonIgnoreProperties("comments")
    private Comment parent;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "comment")
    //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Vote> votes = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public Comment content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDate() {
        return date;
    }

    public Comment date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getCommenter() {
        return commenter;
    }

    public Comment commenter(User user) {
        this.commenter = user;
        return this;
    }

    public void setCommenter(User user) {
        this.commenter = user;
    }

    public Report getReport() {
        return report;
    }

    public Comment report(Report report) {
        this.report = report;
        return this;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Comment getParent() {
        return parent;
    }

    public Comment parent(Comment comment) {
        this.parent = comment;
        return this;
    }

    public void setParent(Comment comment) {
        this.parent = comment;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public Comment votes(Set<Vote> votes) {
        this.votes = votes;
        return this;
    }

    public Comment addVote(Vote vote) {
        this.votes.add(vote);
        vote.setComment(this);
        return this;
    }

    public Comment removeVote(Vote vote) {
        this.votes.remove(vote);
        vote.setComment(null);
        return this;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
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
        Comment comment = (Comment) o;
        if (comment.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
