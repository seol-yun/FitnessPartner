package fitnessapp.fitnesspartner.controller;
import fitnessapp.fitnesspartner.domain.Friend;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.dto.FriendInfoDTO;
import fitnessapp.fitnesspartner.service.FriendService;
import fitnessapp.fitnesspartner.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/add")
    public void addFriend(@RequestBody AddFriendRequest request) {
        friendService.addFriend(request.getMemberId(), request.getFriendMemberId());
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFriend(@RequestBody DeleteFriendRequest request, HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        if (session != null && session.getAttribute("loginId") != null) {
            String loginId = (String) session.getAttribute("loginId");
            friendService.deleteFriend(loginId, request.getFriendMemberId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<FriendInfoDTO>> getAllFriends(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginId") != null) {
            String loginId = (String) session.getAttribute("loginId");
            List<FriendInfoDTO> friends = friendService.getAllFriends(loginId);
            return ResponseEntity.ok().body(friends);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @Getter @Setter
    static class AddFriendRequest {
        private String memberId;
        private String friendMemberId;
    }
    @Getter @Setter
    static class DeleteFriendRequest {
        private String friendMemberId;
    }
}

