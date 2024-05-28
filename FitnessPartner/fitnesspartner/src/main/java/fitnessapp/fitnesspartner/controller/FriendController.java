package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.config.JwtUtil;
import fitnessapp.fitnesspartner.dto.FriendInfoDTO;
import fitnessapp.fitnesspartner.service.FriendService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Tag(name = "Friend Controller", description = "친구 관리 API")
public class FriendController {

    private final FriendService friendService;
    private final JwtUtil jwtUtil;

    @PostMapping("/add")
    @Operation(summary = "친구 추가", description = "친구를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> addFriend(
            @Parameter(description = "친구 추가 요청", required = true) @RequestBody AddFriendRequest request,
            @Parameter(description = "HTTP 요청 객체", required = true) HttpServletRequest servletRequest) {
        String token = extractToken(servletRequest);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String memberId = jwtUtil.extractUsername(token);
            friendService.addFriend(memberId, request.getFriendMemberId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "친구 삭제", description = "친구를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteFriend(
            @Parameter(description = "친구 삭제 요청", required = true) @RequestBody DeleteFriendRequest request,
            @Parameter(description = "HTTP 요청 객체", required = true) HttpServletRequest servletRequest) {
        String token = extractToken(servletRequest);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String memberId = jwtUtil.extractUsername(token);
            friendService.deleteFriend(memberId, request.getFriendMemberId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/all")
    @Operation(summary = "모든 친구 목록 조회", description = "로그인된 사용자의 모든 친구 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록 반환", content = @Content(schema = @Schema(implementation = FriendInfoDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<FriendInfoDTO>> getAllFriends(
            @Parameter(description = "HTTP 요청 객체", required = true) HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String memberId = jwtUtil.extractUsername(token);
            List<FriendInfoDTO> friends = friendService.getAllFriends(memberId);
            return ResponseEntity.ok().body(friends);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Getter @Setter
    static class AddFriendRequest {
        @Parameter(description = "친구 추가할 회원 ID", required = true)
        private String friendMemberId;
    }

    @Getter @Setter
    static class DeleteFriendRequest {
        @Parameter(description = "친구 삭제할 회원 ID", required = true)
        private String friendMemberId;
    }
}
