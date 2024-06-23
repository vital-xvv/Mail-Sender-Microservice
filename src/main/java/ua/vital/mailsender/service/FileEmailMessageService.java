package ua.vital.mailsender.service;

import ua.vital.mailsender.document.FileEmailMessage;
import ua.vital.mailsender.mb_message.FileMessage;

public interface FileEmailMessageService {
    void save(FileMessage fileMessage);
    FileEmailMessage findById(String fileEmailMessageId);
}
