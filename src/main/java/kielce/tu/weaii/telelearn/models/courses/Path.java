package kielce.tu.weaii.telelearn.models.courses;

import lombok.Data;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "Paths")
public class Path {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false)
    @Lob
    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "courseId")
    private Course course;

    @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "path")
    private List<Task> tasks;
}
