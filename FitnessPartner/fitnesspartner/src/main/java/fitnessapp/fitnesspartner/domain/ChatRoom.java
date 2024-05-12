package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter @Setter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user1;
    private String user2;
    private String roomId;

    public static ChatRoom create(String user1, String user2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.user1 = user1;
        chatRoom.user2 = user2;
        chatRoom.roomId = UUID.randomUUID().toString();
        return chatRoom;
    }
}
