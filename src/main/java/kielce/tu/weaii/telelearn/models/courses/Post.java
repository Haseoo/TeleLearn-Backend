package kielce.tu.weaii.telelearn.models.courses;

import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.models.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "POSTS")
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;

    @Lob
    private String content;

    @JoinColumn(nullable = false)
    private PostVisibility postVisibility;

    @JoinColumn(nullable = false)
    private LocalDateTime publicationTime;

    @JoinColumn(nullable = false)
    private boolean commentingAllowed;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "courseId")
    private Course course;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "authorId")
    private User author;

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true, mappedBy = "post")
    private List<Attachment> attachments;

    @OneToMany(fetch = LAZY, mappedBy = "post", cascade = ALL)
    private List<Comment> comments;
}
