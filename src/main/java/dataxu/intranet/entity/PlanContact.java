package dataxu.intranet.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "plan_contact")
public class PlanContact implements Comparable<PlanContact> {
    @Id
    @SequenceGenerator(name = "plan_contact_seq", sequenceName = "plan_contact_id_seq")
    @GeneratedValue(generator = "plan_contact_seq", strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "plan_id")
    private Integer planId;

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Column(name = "chapter_id")
    private Integer chapterId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public String toString() {
        return "PlanContact [id=" + id + ", planId=" + planId + ", contact=" + contact + ", chapterId=" + chapterId
                + "]";
    }

    @Override
    public int compareTo(PlanContact o) {
        int result = this.contact.getFirstName().compareTo(o.contact.getFirstName());
        if (result == 0) {
            result = this.contact.getLastName().compareTo(o.contact.getLastName());
        }

        return result;
    }
}
