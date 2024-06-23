package ua.vital.mailsender.scheduled;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.vital.mailsender.document.FileEmailMessage;
import ua.vital.mailsender.enumeration.EmailMessageStatus;
import ua.vital.mailsender.repository.FileEmailMessageRepository;
import ua.vital.mailsender.service.EmailSenderService;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final FileEmailMessageRepository fileEmailMessageRepository;
    private final EmailSenderService emailSenderService;

    @Scheduled(cron = "0 */5 * ? * *")
    public void resendFailedFileEmailMessages() {
        Page<FileEmailMessage> page = fileEmailMessageRepository
                .findAllByStatus(EmailMessageStatus.EMAIL_SEND_FAILURE, PageRequest.of(0,100));
        page.forEach(this::resendEmail);

        while(page.hasNext()){
            page =fileEmailMessageRepository
                    .findAllByStatus(EmailMessageStatus.EMAIL_SEND_FAILURE, page.nextPageable());
            page.forEach(this::resendEmail);
        }
    }

    private void resendEmail(FileEmailMessage message){
        try{
            emailSenderService.sendSimpleEmail(message.getFromEmail(), message.getToEmail(),
                    message.getBody(), message.getSubject());
            message.setStatus(EmailMessageStatus.EMAIL_SENT);
        }catch (MailException ex) {
            message.setErrorMessage("%s: %s)".formatted(ex.getClass().getName(), ex.getMessage()));
        }
        message.setAttemptCount(message.getAttemptCount() + 1);
        message.setLastAttemptTime(Instant.now());
        fileEmailMessageRepository.save(message);
    }
}
