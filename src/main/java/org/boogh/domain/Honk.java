package org.boogh.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Honk.
 */
@Entity
@Table(name = "honk")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Honk implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "honked", nullable = false)
    private Boolean honked;

    @ManyToOne(optional = false)
    @NotNull
    private Report report;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("honks")
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isHonked() {
        return honked;
    }

    public Honk honked(Boolean honked) {
        this.honked = honked;
        return this;
    }

    public void setHonked(Boolean honked) {
        this.honked = honked;
    }

    public Report getReport() {
        return report;
    }

    public Honk report(Report report) {
        this.report = report;
        return this;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public User getUser() {
        return user;
    }

    public Honk user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
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
        Honk honk = (Honk) o;
        if (honk.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), honk.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Honk{" +
            "id=" + getId() +
            ", honked='" + isHonked() + "'" +
            "}";
    }
}
