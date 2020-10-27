package kielce.tu.weaii.telelearn.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "STUDENTS")
public class Student extends User {
    @Column(columnDefinition = "TEXT")
    private String unit;
}
