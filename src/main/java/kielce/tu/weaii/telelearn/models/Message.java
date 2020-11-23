package kielce.tu.weaii.telelearn.models;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "MESSAGES")
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(insertable = false, nullable = false)
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "SENDER_ID")
    private User sender;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "RECEIVER_ID")
    private User receiver;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private LocalDateTime sendTime;
    private boolean read = false;
}
