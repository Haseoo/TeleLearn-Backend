package kielce.tu.weaii.telelearn.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "GLOBAL_NEWS")
public class GlobalNews {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "authorId")
    private User author;
    @Column(columnDefinition = "TEXT")
    private String brief;
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    @Column(nullable = false)
    private LocalDateTime publicationDate;
}
