package ua.vital.mailsender.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ua.vital.mailsender.service.EmailSenderService;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {
    private final JavaMailSender mailSender;

    @Override
    public void sendSimpleEmail(String fromEmail, String toEmail, String body, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
    }
}
