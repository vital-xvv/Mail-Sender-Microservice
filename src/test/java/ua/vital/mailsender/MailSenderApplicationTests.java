package ua.vital.mailsender;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import ua.vital.mailsender.document.FileEmailMessage;
import ua.vital.mailsender.enumeration.EmailMessageStatus;
import ua.vital.mailsender.enumeration.FileEventType;
import ua.vital.mailsender.mb_listener.FileMessageListener;
import ua.vital.mailsender.mb_message.FileMessage;
import ua.vital.mailsender.repository.FileEmailMessageRepository;
import ua.vital.mailsender.scheduled.ScheduledTasks;
import ua.vital.mailsender.service.EmailSenderService;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MailSenderApplication.class
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@ActiveProfiles(value = "test")
class MailSenderApplicationTests {
    @Value("${FROM_EMAIL}")
    private String fromEmail;
    private final FileMessageListener fileMessageListener;
    private final FileEmailMessageRepository fileEmailMessageRepository;
    private final ScheduledTasks scheduledTasks;

    @MockBean
    private final EmailSenderService mailSender;

    @BeforeEach
    public void clearElastic(){
        fileEmailMessageRepository.deleteAll();
    }

    @Test
    public void emailIsNotSentAndAppropriateObjectSavedWhenMailAuthenticationException(){
        FileMessage fileMessage = new FileMessage(2, FileEventType.FILE_CREATED, "reciever_example@gmail.com", "Fake subject", "Fake body");
        doThrow(MailAuthenticationException.class).when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body", "Fake subject");

        fileMessageListener.listener(fileMessage);

        FileEmailMessage message = fileEmailMessageRepository.findAll().iterator().next();

        assertNotNull(message);
        assertEquals("reciever_example@gmail.com", message.getToEmail());
        assertEquals(EmailMessageStatus.EMAIL_SEND_FAILURE, message.getStatus());
        assertEquals(1, message.getAttemptCount());
        assertEquals(fromEmail, message.getFromEmail());
        assertEquals("Fake subject", message.getSubject());
        assertEquals("Fake body", message.getBody());
    }

    @Test
    public void emailIsNotSentAndAppropriateObjectSavedWhenMailSendException(){
        FileMessage fileMessage = new FileMessage(2, FileEventType.FILE_CREATED, "reciever_example@gmail.com", "Fake subject", "Fake body");
        doThrow(MailSendException.class).when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body", "Fake subject");

        fileMessageListener.listener(fileMessage);

        FileEmailMessage message = fileEmailMessageRepository.findAll().iterator().next();

        assertNotNull(message);
        assertEquals("reciever_example@gmail.com", message.getToEmail());
        assertEquals(EmailMessageStatus.EMAIL_SEND_FAILURE, message.getStatus());
        assertEquals(1, message.getAttemptCount());
        assertEquals(fromEmail, message.getFromEmail());
        assertEquals("Fake subject", message.getSubject());
        assertEquals("Fake body", message.getBody());
    }

    @Test
    public void emailIsSentAndAppropriateObjectSaved(){
        FileMessage fileMessage = new FileMessage(2, FileEventType.FILE_CREATED, "reciever_example@gmail.com", "Fake subject", "Fake body");

        fileMessageListener.listener(fileMessage);

        FileEmailMessage message = fileEmailMessageRepository.findAll().iterator().next();

        assertNotNull(message);
        assertEquals("reciever_example@gmail.com", message.getToEmail());
        assertEquals(EmailMessageStatus.EMAIL_SENT, message.getStatus());
        assertEquals(1, message.getAttemptCount());
        assertEquals(fromEmail, message.getFromEmail());
        assertEquals("Fake subject", message.getSubject());
        assertEquals("Fake body", message.getBody());
    }

    @Test
    public void schedulerWillResendAllFailedMessagesWhenMailServerStabilized(){
        FileMessage fileMessage = new FileMessage(1, FileEventType.FILE_CREATED, "reciever_example@gmail.com", "Fake subject", "Fake body");
        FileMessage fileMessage2 = new FileMessage(2, FileEventType.FILE_CREATED, "reciever_example@gmail.com", "Fake subject2", "Fake body2");

        doThrow(MailSendException.class).when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body", "Fake subject");
        doThrow(MailAuthenticationException.class).when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body2", "Fake subject2");

        fileMessageListener.listener(fileMessage);
        fileMessageListener.listener(fileMessage2);

        assertEquals(2, fileEmailMessageRepository.findAllByStatus(EmailMessageStatus.EMAIL_SEND_FAILURE).size());
        Instant previousAttemptTime = fileEmailMessageRepository.findAllByStatus(EmailMessageStatus.EMAIL_SEND_FAILURE).get(0).getLastAttemptTime();

        doNothing().when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body", "Fake subject");
        doNothing().when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body2", "Fake subject2");

        scheduledTasks.resendFailedFileEmailMessages();

        List<FileEmailMessage> fileEmailSentMessageList = fileEmailMessageRepository.findAllByStatus(EmailMessageStatus.EMAIL_SENT);

        assertEquals(2, fileEmailSentMessageList.size());
        assertTrue(fileEmailSentMessageList.stream().allMatch(f -> f.getAttemptCount() == 2));
        assertTrue(fileEmailSentMessageList.stream().allMatch(f -> f.getLastAttemptTime().isAfter(previousAttemptTime)));
    }

    @Test
    public void schedulerWillNotResendAllFailedMessagesBecauseMailServerDidntStabilize(){
        FileMessage fileMessage = new FileMessage(1, FileEventType.FILE_CREATED, "reciever_example@gmail.com", "Fake subject", "Fake body");
        FileMessage fileMessage2 = new FileMessage(2, FileEventType.FILE_CREATED, "reciever_example@gmail.com", "Fake subject2", "Fake body2");

        doThrow(MailSendException.class).when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body", "Fake subject");
        doThrow(MailAuthenticationException.class).when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body2", "Fake subject2");

        fileMessageListener.listener(fileMessage);
        fileMessageListener.listener(fileMessage2);

        assertEquals(2, fileEmailMessageRepository.findAllByStatus(EmailMessageStatus.EMAIL_SEND_FAILURE).size());
        Instant previousAttemptTime = fileEmailMessageRepository.findAllByStatus(EmailMessageStatus.EMAIL_SEND_FAILURE).get(0).getLastAttemptTime();

        doThrow(MailAuthenticationException.class).when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body", "Fake subject");
        doThrow(MailSendException.class).when(mailSender).sendSimpleEmail(fromEmail, "reciever_example@gmail.com", "Fake body2", "Fake subject2");

        scheduledTasks.resendFailedFileEmailMessages();

        List<FileEmailMessage> fileEmailFailedMessageList = fileEmailMessageRepository.findAllByStatus(EmailMessageStatus.EMAIL_SEND_FAILURE);

        assertEquals(2, fileEmailFailedMessageList.size());
        assertTrue(fileEmailFailedMessageList.stream().allMatch(f -> f.getAttemptCount() == 2));
        assertTrue(fileEmailFailedMessageList.stream().allMatch(f -> f.getLastAttemptTime().isAfter(previousAttemptTime)));
    }

}
