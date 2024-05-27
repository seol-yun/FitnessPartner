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

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final JwtUtil jwtUtil;

    @PostMapping("/add")
    public ResponseEntity<Void> addFriend(@RequestBody AddFriendRequest request, HttpServletRequest servletRequest) {
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
    public ResponseEntity<Void> deleteFriend(@RequestBody DeleteFriendRequest request, HttpServletRequest servletRequest) {
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
    public ResponseEntity<List<FriendInfoDTO>> getAllFriends(HttpServletRequest request) {
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
        private String friendMemberId;
    }

    @Getter @Setter
    static class DeleteFriendRequest {
        private String friendMemberId;
    }
}
