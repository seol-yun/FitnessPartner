package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.ChatRoom;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.domain.ChatMessage;
import fitnessapp.fitnesspartner.dto.ChatRoomDTO;
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
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/chat/room/{roomId}/details")
    public ChatRoomDTO getChatRoomDetails(HttpServletRequest request, @PathVariable("roomId") String roomId) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginId") != null) {
            String loginId = (String) session.getAttribute("loginId");

            Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findRoomById(roomId);
            if (optionalChatRoom.isPresent()) {
                ChatRoom chatRoom = optionalChatRoom.get();

                ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
                chatRoomDTO.setRoomId(chatRoom.getRoomId());
                chatRoomDTO.setTimeStamp(chatRoom.getTimestamp());
                Member me = null, other = null;

                if (chatRoom.getUser1().equals(loginId)) {
                    me = memberRepository.findOne(chatRoom.getUser1());
                    other = memberRepository.findOne(chatRoom.getUser2());
                } else if (chatRoom.getUser2().equals(loginId)) {
                    me = memberRepository.findOne(chatRoom.getUser2());
                    other = memberRepository.findOne(chatRoom.getUser1());
                }

                chatRoomDTO.setMyId(me.getId());
                chatRoomDTO.setMyName(me.getName());
                chatRoomDTO.setOtherId(other.getId());
                chatRoomDTO.setOtherName(other.getName());
                return chatRoomDTO;
            } else {
                throw new IllegalArgumentException("Invalid room ID");
            }
        } else {
            throw new IllegalStateException("User not authenticated");
        }
    }

    @MessageMapping("chat/message")
    public void message(@Payload ChatMessage message) {
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/send-message/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") String roomId, @Payload Map<String, String> message) {
        String content = message.get("content");
        String sender = message.get("sender");

        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(roomId);
        chatMessage.setSender(sender);
        chatMessage.setMessage(content);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);

        // Update chatRoom timestamp
        chatRoom.setTimestamp(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("sender", sender);
        responseMessage.put("content", content);

        messagingTemplate.convertAndSend("/topic/messages/" + roomId, responseMessage);
    }





    @GetMapping("/chat/messages/{roomId}")
    public List<ChatMessage> getMessages(@PathVariable("roomId") String roomId) {
        chatRoomRepository.findRoomById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}