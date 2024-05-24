package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.ChatRoom;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.dto.ChatRoomDTO;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor

public class ChatRoomRepository {
    private final EntityManager em;
    private final MemberRepository memberRepository;

    /**
     * 모든 채팅방 조회
     * @param request
     * @return
     */
    public List<ChatRoomDTO> findAllRoom(HttpServletRequest request) {
        List<ChatRoomDTO> userChatRooms = new ArrayList<>();
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("loginId") != null) {
            String currentUserId = (String) session.getAttribute("loginId"); // 세션에서 현재 사용자의 ID 가져오기
            List<ChatRoom> allChatRooms = em.createQuery("SELECT c FROM ChatRoom c", ChatRoom.class).getResultList();

            for (ChatRoom chatRoom : allChatRooms) {
                String user1 = chatRoom.getUser1();
                String user2 = chatRoom.getUser2();
                Member me = null, other = null;

                if (user1 != null && user1.equals(currentUserId)) {
                    me = memberRepository.findOne(user1);
                    if (user2 != null) {
                        other = memberRepository.findOne(user2);
                    }
                } else if (user2 != null && user2.equals(currentUserId)) {
                    me = memberRepository.findOne(user2);
                    if (user1 != null) {
                        other = memberRepository.findOne(user1);
                    }
                } else{
                    continue;
                }

                // Create ChatRoomDTO
                ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
                chatRoomDTO.setRoomId(chatRoom.getRoomId());
                chatRoomDTO.setMyId(me.getId());
                chatRoomDTO.setMyName(me.getName());
                chatRoomDTO.setOtherId(other != null ? other.getId() : null);
                chatRoomDTO.setOtherName(other != null ? other.getName() : null);
                chatRoomDTO.setTimeStamp(chatRoom.getTimestamp());
                userChatRooms.add(chatRoomDTO);
            }

            // 최신 순으로 정렬
            Collections.sort(userChatRooms, Comparator.comparing(ChatRoomDTO::getTimeStamp).reversed());
        }

        return userChatRooms;
    }




    /**
     * 채팅방 ID로 특정 채팅방 조회
     * @param id
     * @return
     */
    public Optional<ChatRoom> findRoomById(String id) {
        return em.createQuery("SELECT c FROM ChatRoom c WHERE c.roomId = :id", ChatRoom.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    /**
     * 이미 방이 존재하는지 확인
     * @param name1 내이름
     * @param name2 친구이름
     * @return
     */
    public ChatRoom findExistingRoom(String name1, String name2) {
        return em.createQuery("select cr from ChatRoom cr where (cr.user1 = :name1 and cr.user2 = :name2) or (cr.user1 = :name2 and cr.user2 = :name1)", ChatRoom.class)
                .setParameter("name1", name1)
                .setParameter("name2", name2)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }


    /**
     * 방이없으면 채팅방 생성
     * @param name1
     * @param name2
     * @return
     */
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

    /**
     * 채팅방 수정 저장
     * @param chatRoom
     */
    @Transactional
    public void save(ChatRoom chatRoom) {
        em.merge(chatRoom);
    }

    /**
     * 채팅방 나가기(userId가 나감)
     * @param roomId
     * @param userId
     * @return
     */
    @Transactional
    public boolean leaveChatRoom(String roomId, String userId) {
        ChatRoom chatRoom = findRoomById(roomId).orElse(null);
        if (chatRoom == null) {
            return false;
        }
        if (chatRoom.getUser1().equals(userId)) {
            chatRoom.setUser1(null);
        } else if (chatRoom.getUser2().equals(userId)) {
            chatRoom.setUser2(null);
        } else {
            return false;
        }

        save(chatRoom);
        return true;
    }
}
