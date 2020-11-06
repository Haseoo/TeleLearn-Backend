package kielce.tu.weaii.telelearn.models.courses;

import kielce.tu.weaii.telelearn.models.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @JoinColumn(nullable = false)
    private LocalDateTime publicationTime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "authorId")
    private User author;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "postId")
    private Post post;
}
