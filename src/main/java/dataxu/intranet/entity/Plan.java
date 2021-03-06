package dataxu.intranet.entity;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Entity
@Table(name = "plan")
public class Plan implements Comparable<Plan> {
    @Id
    @SequenceGenerator(name = "plan_seq", sequenceName = "plan_id_seq")
    @GeneratedValue(generator = "plan_seq", strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "plan_id")
    private Set<PlanContact> planContacts = Sets.newHashSet();

    @Column(name = "maintenance_allowance", nullable = false)
    private Integer maintenanceAllowance;

    public Integer getMaintenanceAllowance() {
        return maintenanceAllowance;
    }

    public void setMaintenanceAllowance(Integer maintenanceAllowance) {
        this.maintenanceAllowance = maintenanceAllowance;
    }

    @PrePersist
    protected void prePersist() {
        Date date = new Date();
        this.createdOn = date;
        this.updatedOn = date;
    }

    public List<PlanContact> getPlanContacts() {
        List<PlanContact> contacts = Lists.newArrayList(planContacts);
        Collections.sort(contacts);
        return contacts;
    }

    public void setPlanContacts(Set<PlanContact> planContacts) {
        this.planContacts = planContacts;
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedOn = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public int compareTo(Plan o) {
        return this.startDate.compareTo(o.startDate);
    }
}
