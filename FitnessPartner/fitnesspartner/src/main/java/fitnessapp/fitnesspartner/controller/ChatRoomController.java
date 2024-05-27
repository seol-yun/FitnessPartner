package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.config.JwtUtil;
import fitnessapp.fitnesspartner.domain.ChatRoom;
import fitnessapp.fitnesspartner.dto.ChatRoomDTO;
import fitnessapp.fitnesspartner.repository.ChatRoomRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final JwtUtil jwtUtil;

    /**
     * 모든 채팅방 목록 반환
     * @param request
     * @return
     */
    @GetMapping("/rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDTO>> room(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            List<ChatRoomDTO> rooms = chatRoomRepository.findAllRoom(jwtUtil.extractUsername(token));
            return ResponseEntity.ok().body(rooms);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * 채팅방 생성
     * @param request
     * @param user2
     * @return
     */
    @ResponseBody
    @PostMapping("/room")
    public ResponseEntity<String> createRoom(HttpServletRequest request, @RequestParam("user2") String user2) {
        String token = extractToken(request);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String user1 = jwtUtil.extractUsername(token);
            ChatRoom newRoom = chatRoomRepository.createChatRoom(user1, user2);
            return ResponseEntity.ok(newRoom.getRoomId()); // 생성된 채팅방의 ID 반환
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }
    }

    /**
     * 채팅방 나가기
     * @param request
     * @param roomId
     * @return
     */
    @PostMapping("/room/leave")
    public ResponseEntity<String> leaveRoom(HttpServletRequest request, @RequestParam("roomId") String roomId) {
        String token = extractToken(request);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String userId = jwtUtil.extractUsername(token);
            boolean result = chatRoomRepository.leaveChatRoom(roomId, userId);
            return result ? ResponseEntity.ok("success") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failure");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
