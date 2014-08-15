package dataxu.intranet.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "chapter")
public class Chapter implements Comparable<Chapter> {
    @Id
    @SequenceGenerator(name = "contact_seq", sequenceName = "contact_id_seq")
    @GeneratedValue(generator = "contact_seq", strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

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

    @Override
    public int compareTo(Chapter o) {
        return name.compareTo(o.name);
    }
}
