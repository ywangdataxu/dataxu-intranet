package dataxu.intranet.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "contact_velocity")
public class ContactVelocity implements Comparable<ContactVelocity> {
    @Id
    @SequenceGenerator(name = "contact_seq", sequenceName = "contact_id_seq")
    @GeneratedValue(generator = "contact_seq", strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "contact_id", insertable = false, updatable = false, nullable = false)
    private Contact contact; // NOPMD

    @OneToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @Column(name = "velocity")
    private Integer velocity;

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

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public Integer getVelocity() {
        return velocity;
    }

    public void setVelocity(Integer velocity) {
        this.velocity = velocity;
    }

    @Override
    public int compareTo(ContactVelocity o) {
        return this.chapter.getName().compareTo(o.chapter.getName());
    }
}
