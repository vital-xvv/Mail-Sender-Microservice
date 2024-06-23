package ua.vital.mailsender.service;

public interface EmailSenderService {
    void sendSimpleEmail(String fromEmail, String toEmail, String body, String subject);
}
