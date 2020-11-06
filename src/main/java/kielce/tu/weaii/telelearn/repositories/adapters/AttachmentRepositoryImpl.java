package kielce.tu.weaii.telelearn.repositories.adapters;

import kielce.tu.weaii.telelearn.models.Attachment;
import kielce.tu.weaii.telelearn.repositories.jpa.AttachmentJPARepository;
import kielce.tu.weaii.telelearn.repositories.ports.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class AttachmentRepositoryImpl implements AttachmentRepository {
    private final AttachmentJPARepository jpaRepository;

    @Override
    public Optional<Attachment> getById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Attachment store(Attachment attachment) {
        return jpaRepository.saveAndFlush(attachment);
    }

    @Override
    public void delete(Attachment attachment) {
        jpaRepository.delete(attachment);
    }
}
