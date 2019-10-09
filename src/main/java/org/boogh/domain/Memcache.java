package org.boogh.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Memcache.
 */
@Entity
@Table(name = "memcache")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Memcache implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "jhi_hash", nullable = false)
    private String hash;

    @NotNull
    @Column(name = "telegram_id", nullable = false)
    private Long telegramId;

    @OneToOne(optional = false)    @NotNull

    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public Memcache hash(String hash) {
        this.hash = hash;
        return this;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public Memcache telegramId(Long telegramId) {
        this.telegramId = telegramId;
        return this;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public User getUser() {
        return user;
    }

    public Memcache user(User user) {
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
        Memcache memcache = (Memcache) o;
        if (memcache.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), memcache.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Memcache{" +
            "id=" + getId() +
            ", hash='" + getHash() + "'" +
            ", telegramId=" + getTelegramId() +
            "}";
    }
}
