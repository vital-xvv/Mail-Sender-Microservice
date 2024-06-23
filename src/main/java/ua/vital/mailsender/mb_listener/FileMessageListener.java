package ua.vital.mailsender.mb_listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import ua.vital.mailsender.config.MQConfig;
import ua.vital.mailsender.document.FileEmailMessage;
import ua.vital.mailsender.enumeration.EmailMessageStatus;
import ua.vital.mailsender.mb_message.FileMessage;
import ua.vital.mailsender.repository.FileEmailMessageRepository;
import ua.vital.mailsender.service.EmailSenderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileMessageListener {
    @Value("${FROM_EMAIL}")
    private String fromEmail;
    private final EmailSenderService emailSender;
    private final FileEmailMessageRepository fileEmailMessageRepository;

    @RabbitListener(queues = MQConfig.EMAIL_QUEUE)
    public void listener(FileMessage message){
        FileEmailMessage fileEmailMessage = FileEmailMessage.of(message);
        fileEmailMessage.setFromEmail(fromEmail);
        try {
            emailSender.sendSimpleEmail(fileEmailMessage.getFromEmail(), fileEmailMessage.getToEmail(),
                    fileEmailMessage.getBody(), fileEmailMessage.getSubject());
            fileEmailMessage.setStatus(EmailMessageStatus.EMAIL_SENT);
        }catch (MailException ex){
            fileEmailMessage.setErrorMessage("%s: %s)".formatted(ex.getClass().getName(), ex.getMessage()));
            fileEmailMessage.setStatus(EmailMessageStatus.EMAIL_SEND_FAILURE);
            log.error("Error occurred sending an email: {}", ex.getMessage());
        }
        fileEmailMessageRepository.save(fileEmailMessage);
    }
}
