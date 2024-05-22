package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.dto.ChatMessage;
import fitnessapp.fitnesspartner.repository.ChatRoomRepository;
import fitnessapp.fitnesspartner.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
@RestController
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
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

        // 받은 메시지를 다시 클라이언트에게 브로드캐스트합니다.
        messagingTemplate.convertAndSend("/topic/messages/" + roomId, myName + " : " + content);
    }
}
