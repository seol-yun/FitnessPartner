package fitnessapp.fitnesspartner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoomDTO {
    private String roomId;
    private String myId;
    private String myName;
    private String otherId;
    private String otherName;
    private LocalDateTime timeStamp;

    // Constructors, getters, and setters
}
