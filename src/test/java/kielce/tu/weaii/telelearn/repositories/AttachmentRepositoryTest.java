package kielce.tu.weaii.telelearn.repositories;

import kielce.tu.weaii.telelearn.TestData;
import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.repositories.jpa.AttachmentJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.AttachmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;

import static kielce.tu.weaii.telelearn.Constants.INTEGRATION_TEST;

@SpringBootTest
@Tag(INTEGRATION_TEST)
class AttachmentRepositoryTest {

    @Autowired
    private AttachmentJPARepository attachmentJPARepository;

    @Autowired
    private AttachmentRepository sut;

    @BeforeEach
    void setUp() {
        attachmentJPARepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        attachmentJPARepository.deleteAll();
    }

    @Test
    @Transactional
    void should_return_attachment_by_id() {
        //given
        Attachment searched = attachmentJPARepository.saveAndFlush(TestData.getAttachment());
        //when
        Optional<Attachment> out = sut.getById(searched.getId());
        //then
        Assertions.assertThat(out).isPresent();
        Assertions.assertThat(out.get().getId()).isEqualTo(searched.getId());
        Assertions.assertThat(out.get().getFileName()).isEqualTo(searched.getFileName());
        Assertions.assertThat(out.get().getFileType()).isEqualTo(searched.getFileType());
        Assertions.assertThat(out.get().getAttachmentData().get(0).getData()).isEqualTo(searched.getAttachmentData().get(0).getData());
    }

    @Test
    void delete() {
        //given
        Attachment toDelete = attachmentJPARepository.saveAndFlush(TestData.getAttachment());
        Attachment notToDelete = attachmentJPARepository.saveAndFlush(TestData.getAttachment());
        //when
        sut.delete(toDelete);
        //then
        Assertions.assertThat(attachmentJPARepository.findAll())
                .noneMatch(a -> a.getId().equals(toDelete.getId()))
                .anyMatch(a -> a.getId().equals(notToDelete.getId()));

    }
}