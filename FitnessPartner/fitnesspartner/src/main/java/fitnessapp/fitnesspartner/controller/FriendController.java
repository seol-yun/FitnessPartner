package fitnessapp.fitnesspartner.controller;
import fitnessapp.fitnesspartner.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/add")
    public void addFriend(@RequestBody AddFriendRequest request) {
        friendService.addFriend(request.getMemberId(), request.getFriendMemberId());
    }

    static class AddFriendRequest {
        private String memberId;
        private String friendMemberId;

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getFriendMemberId() {
            return friendMemberId;
        }

        public void setFriendMemberId(String friendMemberId) {
            this.friendMemberId = friendMemberId;
        }
    }
}

