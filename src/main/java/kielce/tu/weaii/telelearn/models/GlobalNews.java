package kielce.tu.weaii.telelearn.models;

import lombok.Data;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "GlobalNews")
public class GlobalNews {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String author;
    @Column(columnDefinition = "TEXT")
    private String brief;
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    @Column(nullable = false)
    private LocalDateTime publicationDate;
}
