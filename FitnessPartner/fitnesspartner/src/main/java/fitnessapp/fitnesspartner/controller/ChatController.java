package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.dto.ChatMessage;
import fitnessapp.fitnesspartner.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Map;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("chat/message")
    public void message(ChatMessage message) {
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/send-message")
    public void sendMessage(@Payload Map<String, String> message) {
        String content = message.get("content");
        // 받은 메시지 처리 코드 추가
        System.out.println("Received message content: " + content);

        // 받은 메시지를 다시 클라이언트에게 브로드캐스트합니다.
        messagingTemplate.convertAndSend("/topic/messages", content);
    }

}
