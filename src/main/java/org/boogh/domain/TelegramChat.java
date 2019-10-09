package org.boogh.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TelegramChat.
 */
@Entity
@Table(name = "telegram_chat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TelegramChat implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    @NotNull
    @Column(name = "telegram_user_id", nullable = false, unique = true)
    private Long telegramUserId;

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

    public Long getChatId() {
        return chatId;
    }

    public TelegramChat chatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getTelegramUserId() {
        return telegramUserId;
    }

    public TelegramChat telegramUserId(Long telegramUserId) {
        this.telegramUserId = telegramUserId;
        return this;
    }

    public void setTelegramUserId(Long telegramUserId) {
        this.telegramUserId = telegramUserId;
    }

    public User getUser() {
        return user;
    }

    public TelegramChat user(User user) {
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
        TelegramChat telegramChat = (TelegramChat) o;
        if (telegramChat.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), telegramChat.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TelegramChat{" +
            "id=" + getId() +
            ", chatId=" + getChatId() +
            ", telegramUserId=" + getTelegramUserId() +
            "}";
    }
}
