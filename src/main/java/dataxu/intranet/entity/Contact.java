package dataxu.intranet.entity;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @SequenceGenerator(name = "contact_seq", sequenceName = "contact_id_seq")
    @GeneratedValue(generator = "contact_seq", strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "created_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_id", nullable = false)
    @JsonManagedReference
    private List<ContactVelocity> velocities;

    public List<ContactVelocity> getVelocities() {
        Collections.sort(velocities);
        return velocities;
    }

    public void setVelocities(List<ContactVelocity> velocities) {
        this.velocities = velocities;
    }

    @PrePersist
    protected void prePersist() {
        Date date = new Date();
        this.createdOn = date;
        this.updatedOn = date;
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedOn = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    @Override
    public String toString() {
        return "Contact [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName
                + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + ", velocities=" + velocities + "]";
    }
}
