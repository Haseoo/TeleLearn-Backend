package kielce.tu.weaii.telelearn.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "MESSAGES")
public class Message {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "senderId")
    private User sender;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "receiverId")
    private User receiver;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private LocalDateTime sendTime;
    private boolean read = false;
}
