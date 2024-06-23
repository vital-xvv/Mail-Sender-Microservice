package ua.vital.mailsender.mb_message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.vital.mailsender.enumeration.FileEventType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMessage {
    private Integer fileId;
    private FileEventType eventType;
    private String receiverEmail;
    private String emailSubject;
    private String emailBody;
}
