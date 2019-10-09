package org.boogh.service.dto;

import io.github.jhipster.service.filter.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the Reporter entity. This class is used in ReporterResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /reporters?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ReporterCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter karma;

    private BooleanFilter visibility;

    private BooleanFilter moderator;

    private StringFilter location;

    private BooleanFilter notificationsOn;

    private LongFilter userId;

    public ReporterCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getKarma() {
        return karma;
    }

    public void setKarma(IntegerFilter karma) {
        this.karma = karma;
    }

    public BooleanFilter getVisibility() {
        return visibility;
    }

    public void setVisibility(BooleanFilter visibility) {
        this.visibility = visibility;
    }

    public BooleanFilter getModerator() {
        return moderator;
    }

    public void setModerator(BooleanFilter moderator) {
        this.moderator = moderator;
    }

    public StringFilter getLocation() {
        return location;
    }

    public void setLocation(StringFilter location) {
        this.location = location;
    }

    public BooleanFilter getNotificationsOn() {
        return notificationsOn;
    }

    public void setNotificationsOn(BooleanFilter notificationsOn) {
        this.notificationsOn = notificationsOn;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReporterCriteria that = (ReporterCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(karma, that.karma) &&
            Objects.equals(visibility, that.visibility) &&
            Objects.equals(moderator, that.moderator) &&
            Objects.equals(location, that.location) &&
            Objects.equals(notificationsOn, that.notificationsOn) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        karma,
        visibility,
        moderator,
        location,
        notificationsOn,
        userId
        );
    }

    @Override
    public String toString() {
        return "ReporterCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (karma != null ? "karma=" + karma + ", " : "") +
                (visibility != null ? "visibility=" + visibility + ", " : "") +
                (moderator != null ? "moderator=" + moderator + ", " : "") +
                (location != null ? "location=" + location + ", " : "") +
                (notificationsOn != null ? "notificationsOn=" + notificationsOn + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
