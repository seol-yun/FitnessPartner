package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.ChatRoom;
import fitnessapp.fitnesspartner.dto.ChatRoomDTO;
import fitnessapp.fitnesspartner.repository.ChatRoomRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDTO> room(HttpServletRequest request) {
        return chatRoomRepository.findAllRoom(request);
    }
    // 채팅방 생성
    @ResponseBody
    @PostMapping("/room")
    public String createRoom(HttpServletRequest request, @RequestParam("user2") String user2) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginId") != null) {
            String user1 = (String) session.getAttribute("loginId");
            ChatRoom newRoom = chatRoomRepository.createChatRoom(user1, user2);
            System.out.println(newRoom.getRoomId());
            return newRoom.getRoomId(); // 생성된 채팅방의 ID 반환
        } else {
            return null;
        }
    }

//    // 특정 채팅방 조회
//    @GetMapping("/room/{roomId}")
//    @ResponseBody
//    public ChatRoom roomInfo(@PathVariable String roomId) {
//        return chatRoomRepository.findRoomById(roomId);
//    }
}