package org.boogh.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Reporter.
 */
@Entity
@Table(name = "reporter")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Reporter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Lob
    @Column(name = "about")
    private String about;

    @Column(name = "karma")
    private Integer karma;

    @Column(name = "visibility")
    private Boolean visibility;

    @Column(name = "moderator")
    private Boolean moderator;

    @Column(name = "location")
    private String location;

    @Column(name = "notifications_on")
    private Boolean notificationsOn;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAbout() {
        return about;
    }

    public Reporter about(String about) {
        this.about = about;
        return this;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Integer getKarma() {
        return karma;
    }

    public Reporter karma(Integer karma) {
        this.karma = karma;
        return this;
    }

    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    public Boolean isVisibility() {
        return visibility;
    }

    public Reporter visibility(Boolean visibility) {
        this.visibility = visibility;
        return this;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public Boolean isModerator() {
        return moderator;
    }

    public Reporter moderator(Boolean moderator) {
        this.moderator = moderator;
        return this;
    }

    public void setModerator(Boolean moderator) {
        this.moderator = moderator;
    }

    public String getLocation() {
        return location;
    }

    public Reporter location(String location) {
        this.location = location;
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean isNotificationsOn() {
        return notificationsOn;
    }

    public Reporter notificationsOn(Boolean notificationsOn) {
        this.notificationsOn = notificationsOn;
        return this;
    }

    public void setNotificationsOn(Boolean notificationsOn) {
        this.notificationsOn = notificationsOn;
    }

    public User getUser() {
        return user;
    }

    public Reporter user(User user) {
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
        Reporter reporter = (Reporter) o;
        if (reporter.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), reporter.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Reporter{" +
            "id=" + getId() +
            ", about='" + getAbout() + "'" +
            ", karma=" + getKarma() +
            ", visibility='" + isVisibility() + "'" +
            ", moderator='" + isModerator() + "'" +
            ", location='" + getLocation() + "'" +
            ", notificationsOn='" + isNotificationsOn() + "'" +
            "}";
    }
}
