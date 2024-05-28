package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.config.JwtUtil;
import fitnessapp.fitnesspartner.domain.ChatRoom;
import fitnessapp.fitnesspartner.domain.ChatMessage;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.dto.ChatRoomDTO;
import fitnessapp.fitnesspartner.repository.ChatMessageRepository;
import fitnessapp.fitnesspartner.repository.ChatRoomRepository;
import fitnessapp.fitnesspartner.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Tag(name = "Chat Controller", description = "채팅 관리 API")
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/chat/room/{roomId}/details")
    @Operation(summary = "채팅방 세부 정보 조회", description = "특정 채팅방의 세부 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 세부 정보 반환", content = @Content(schema = @Schema(implementation = ChatRoomDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "채팅방 ID 없음")
    })
    public ChatRoomDTO getChatRoomDetails(
            @Parameter(description = "채팅방 ID", required = true) @PathVariable("roomId") String roomId,
            HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String loginId = jwtUtil.extractUsername(token);

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
    @Operation(summary = "채팅 메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    public void message(
            @Parameter(description = "채팅 메시지 내용", required = true) @Payload ChatMessage message) {
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/send-message/{roomId}")
    @Operation(summary = "채팅 메시지 보내기 및 저장", description = "채팅 메시지를 보내고 DB에 저장합니다.")
    public void sendMessage(
            @Parameter(description = "채팅방 ID", required = true) @DestinationVariable("roomId") String roomId,
            @Parameter(description = "메시지 내용", required = true) @Payload Map<String, String> message) {
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

        chatRoom.setTimestamp(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("sender", sender);
        responseMessage.put("content", content);

        messagingTemplate.convertAndSend("/topic/messages/" + roomId, responseMessage);
    }

    @GetMapping("/chat/messages/{roomId}")
    @Operation(summary = "채팅 메시지 내역 조회", description = "특정 채팅방의 메시지 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅 메시지 내역 반환", content = @Content(schema = @Schema(implementation = ChatMessage.class))),
            @ApiResponse(responseCode = "404", description = "채팅방 ID 없음")
    })
    public List<ChatMessage> getMessages(
            @Parameter(description = "채팅방 ID", required = true) @PathVariable("roomId") String roomId) {
        chatRoomRepository.findRoomById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
