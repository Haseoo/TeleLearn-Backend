package kielce.tu.weaii.telelearn.models;

import kielce.tu.weaii.telelearn.models.courses.Course;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Data
@Table(name = "TEACHERS")
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {
    @Column(columnDefinition = "TEXT")
    private String unit;
    @Column(columnDefinition = "TEXT")
    private String title;

    @OneToMany(fetch = LAZY, mappedBy = "owner", cascade = ALL)
    private List<Course> courses;
}
