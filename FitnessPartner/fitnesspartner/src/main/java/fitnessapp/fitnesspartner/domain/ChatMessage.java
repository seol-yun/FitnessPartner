package fitnessapp.fitnesspartner.domain;

import fitnessapp.fitnesspartner.domain.ChatRoom;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK, EXIT, MATCH, MATCH_REQUEST;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_message_seq")
    @SequenceGenerator(name = "chat_message_seq", sequenceName = "CHAT_MESSAGE_SEQ", allocationSize = 1)
    private Long id;

    private String roomId;
    private String sender;
    private String message;
    private LocalDateTime timestamp;
}
