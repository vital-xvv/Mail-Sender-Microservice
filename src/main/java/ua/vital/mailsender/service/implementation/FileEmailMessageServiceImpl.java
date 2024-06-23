package ua.vital.mailsender.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.vital.mailsender.document.FileEmailMessage;
import ua.vital.mailsender.mb_message.FileMessage;
import ua.vital.mailsender.repository.FileEmailMessageRepository;
import ua.vital.mailsender.service.FileEmailMessageService;

@Service
@RequiredArgsConstructor
public class FileEmailMessageServiceImpl implements FileEmailMessageService {
    private final FileEmailMessageRepository repository;

    @Override
    public void save(FileMessage fileMessage) {
        repository.save(FileEmailMessage.of(fileMessage));
    }

    @Override
    public FileEmailMessage findById(String fileEmailMessageId) {
        return repository.findById(fileEmailMessageId).orElse(null);
    }
}
