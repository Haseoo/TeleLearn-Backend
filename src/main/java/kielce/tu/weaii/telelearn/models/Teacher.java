package kielce.tu.weaii.telelearn.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "TEACHERS")
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {
    @Column(columnDefinition = "TEXT")
    private String unit;
    @Column(columnDefinition = "TEXT")
    private String title;
}
