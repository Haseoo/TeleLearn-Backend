package kielce.tu.weaii.telelearn.models;

import kielce.tu.weaii.telelearn.models.courses.Post;
import kielce.tu.weaii.telelearn.models.courses.Task;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "ATTACHMENTS")
public class Attachment implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String fileType;

    @Column(nullable = false)
    private LocalDateTime uploadTime;

    @Lob
    @Column(nullable = false)
    private byte[] data;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "TASK_ID")
    private Task task;
}
