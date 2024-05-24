package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.ChatRoom;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.domain.ChatMessage;
import fitnessapp.fitnesspartner.repository.ChatMessageRepository;
import fitnessapp.fitnesspartner.repository.ChatRoomRepository;
import fitnessapp.fitnesspartner.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private String myName = "";
    @PostMapping("/set-name")
    public void setName(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");

        // 로그인 ID를 이용하여 회원 정보 조회
        if (loginId != null) {
            Member member = memberRepository.findOne(loginId);
            if (member != null) {
                myName = member.getName(); // 발신자의 이름 설정
            }
        }
    }

    @MessageMapping("chat/message")
    public void message(@Payload ChatMessage message) {
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/send-message/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") String roomId, @Payload Map<String, String> message) {
        String content = message.get("content");
        if (myName == null) {
            throw new IllegalStateException("User name not set");
        }

        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));

        if (content.equals("나가기@@!235234@@!#121@!#%@^!^$!#%!^$^21")) {
            // Handle exit message
            content = "("+myName + "님이 채팅에서 나갔습니다.)";
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRoomId(roomId);
            chatMessage.setSender(myName);
            chatMessage.setMessage(content);
            chatMessage.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(chatMessage);
            messagingTemplate.convertAndSend("/topic/messages/" + roomId,  content);
            return;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(roomId);
        chatMessage.setSender(myName);
        chatMessage.setMessage(content);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);

        // Update chatRoom timestamp
        chatRoom.setTimestamp(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        messagingTemplate.convertAndSend("/topic/messages/" + roomId, myName + " : " + content);
    }


    @GetMapping("/chat/messages/{roomId}")
    public List<ChatMessage> getMessages(@PathVariable("roomId") String roomId) {
        chatRoomRepository.findRoomById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}