package kielce.tu.weaii.telelearn.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "ATTACHMENTS_DATA")
@Getter
@Setter
public class AttachmentData implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false, name = "attachmentId")
    private Attachment attachment;

    @Column(nullable = false)
    @Lob
    private byte[] data;

}
