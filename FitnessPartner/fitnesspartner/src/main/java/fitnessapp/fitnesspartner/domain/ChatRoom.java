package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter @Setter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_room_seq")
    @SequenceGenerator(name = "chat_room_seq", sequenceName = "CHAT_ROOM_SEQ", allocationSize = 1)
    private Long id;

    private String user1;
    private String user2;
    private String roomId;
    private LocalDateTime timestamp;

    public static ChatRoom create(String user1, String user2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.user1 = user1;
        chatRoom.user2 = user2;
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.timestamp = LocalDateTime.now(); // 초기 타임스탬프 설정
        return chatRoom;
    }
}
