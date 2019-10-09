package org.boogh.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A QA.
 */
@Entity
@Table(name = "qa")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class QA implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "question", nullable = false)
    private String question;

    @NotNull
    @Column(name = "answer", nullable = false)
    private String answer;

    @NotNull
    @Column(name = "jhi_order", nullable = false)
    private Integer order;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public QA question(String question) {
        this.question = question;
        return this;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public QA answer(String answer) {
        this.answer = answer;
        return this;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getOrder() {
        return order;
    }

    public QA order(Integer order) {
        this.order = order;
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
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
        QA qA = (QA) o;
        if (qA.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), qA.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "QA{" +
            "id=" + getId() +
            ", question='" + getQuestion() + "'" +
            ", answer='" + getAnswer() + "'" +
            ", order=" + getOrder() +
            "}";
    }
}
