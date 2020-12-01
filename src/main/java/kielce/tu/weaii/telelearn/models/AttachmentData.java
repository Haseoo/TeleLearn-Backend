package kielce.tu.weaii.telelearn.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ATTACHMENTS_DATA")
@Getter
@Setter
public class AttachmentData implements Serializable {
    @Id
    @Column(name = "attachmentId")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "attachmentId")
    private Attachment attachment;

    @Column(nullable = false)
    private byte[] data;
}
