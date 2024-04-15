package fitnessapp.fitnesspartner.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChatRoom {
    private String roomId;
    private String name;

    public static ChatRoom create(String name1, String name2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        return chatRoom;
    }
}