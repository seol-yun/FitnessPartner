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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
@Tag(name = "Chat Room Controller", description = "채팅방 관리 API")
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
    @Operation(summary = "모든 채팅방 목록 조회", description = "로그인된 사용자의 모든 채팅방 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 반환", content = @Content(schema = @Schema(implementation = ChatRoomDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<ChatRoomDTO>> room(
            @Parameter(description = "HTTP 요청 객체", required = true) HttpServletRequest request) {
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
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 생성 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<String> createRoom(
            @Parameter(description = "HTTP 요청 객체", required = true) HttpServletRequest request,
            @Parameter(description = "채팅방 상대방의 사용자 ID", required = true) @RequestParam("user2") String user2) {
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
    @Operation(summary = "채팅방 나가기", description = "사용자가 채팅방을 나갑니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 나가기 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<String> leaveRoom(
            @Parameter(description = "HTTP 요청 객체", required = true) HttpServletRequest request,
            @Parameter(description = "채팅방 ID", required = true) @RequestParam("roomId") String roomId) {
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
