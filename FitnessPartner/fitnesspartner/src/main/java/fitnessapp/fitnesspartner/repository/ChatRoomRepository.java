package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.ChatRoom;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor

public class ChatRoomRepository {
    private final EntityManager em;

    /**
     * 모든 채팅방 조회
     */
    public List<ChatRoom> findAllRoom(HttpServletRequest request) {
        List<ChatRoom> userChatRooms = new ArrayList<>();
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("loginId") != null) {
            String currentUserId = (String) session.getAttribute("loginId"); // 세션에서 현재 사용자의 ID 가져오기
            List<ChatRoom> allChatRooms = em.createQuery("SELECT c FROM ChatRoom c", ChatRoom.class).getResultList();

            for (ChatRoom chatRoom : allChatRooms) {
                if (chatRoom.getUser1().equals(currentUserId) || chatRoom.getUser2().equals(currentUserId)) {
                    userChatRooms.add(chatRoom);
                }
            }
        }

        return userChatRooms;
    }


//    public ChatRoom findRoomById(String id) {
//        return chatRooms.stream()
//                .filter(chatRoom -> chatRoom.getRoomId().equals(id))
//                .findFirst()
//                .orElse(null);
//    }

    public ChatRoom findExistingRoom(String name1, String name2) {
        return em.createQuery("select cr from ChatRoom cr where (cr.user1 = :name1 and cr.user2 = :name2) or (cr.user1 = :name2 and cr.user2 = :name1)", ChatRoom.class)
                .setParameter("name1", name1)
                .setParameter("name2", name2)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }


    @Transactional
    public ChatRoom createChatRoom(String name1, String name2) {
        ChatRoom existingRoom = findExistingRoom(name1, name2);
        if (existingRoom != null) {
            return existingRoom;
        }
        ChatRoom chatRoom = ChatRoom.create(name1, name2);
        em.persist(chatRoom); // EntityManager를 사용하여 새로운 채팅방을 데이터베이스에 저장
        return chatRoom;
    }

}
