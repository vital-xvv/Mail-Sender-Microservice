package ua.vital.mailsender.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ua.vital.mailsender.document.FileEmailMessage;
import ua.vital.mailsender.enumeration.EmailMessageStatus;

import java.util.List;


public interface FileEmailMessageRepository extends ElasticsearchRepository<FileEmailMessage, String> {
   Page<FileEmailMessage> findAllByStatus(EmailMessageStatus status, Pageable pageable);
   List<FileEmailMessage> findAllByStatus(EmailMessageStatus status);
}
