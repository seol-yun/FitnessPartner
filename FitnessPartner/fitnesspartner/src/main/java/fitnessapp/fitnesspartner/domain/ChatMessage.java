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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private LocalDateTime timestamp;

}
