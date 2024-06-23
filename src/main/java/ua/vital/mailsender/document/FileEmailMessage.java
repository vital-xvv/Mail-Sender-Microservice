package ua.vital.mailsender.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import ua.vital.mailsender.enumeration.EmailMessageStatus;
import ua.vital.mailsender.helper.Indices;
import ua.vital.mailsender.mb_message.FileMessage;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Document(indexName = Indices.FILE_EMAIL_MESSAGE_INDEX)
public class FileEmailMessage {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    @Field(type = FieldType.Text)
    private String fromEmail;
    @Field(type = FieldType.Text)
    private String toEmail;
    @Field(type = FieldType.Text)
    private String subject;
    @Field(type = FieldType.Text)
    private String body;
    @Field(type = FieldType.Keyword)
    private EmailMessageStatus status;
    @Field(type = FieldType.Text)
    private String errorMessage;
    @Field(type = FieldType.Long)
    private Long attemptCount;
    @Field(type = FieldType.Date)
    private Instant lastAttemptTime;

    public static FileEmailMessage of(FileMessage fileMessage) {
        return FileEmailMessage.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .toEmail(fileMessage.getReceiverEmail())
                .subject(fileMessage.getEmailSubject())
                .body(fileMessage.getEmailBody())
                .status(EmailMessageStatus.EMAIL_PENDING)
                .attemptCount(1L)
                .lastAttemptTime(Instant.now())
                .build();
    }
}
