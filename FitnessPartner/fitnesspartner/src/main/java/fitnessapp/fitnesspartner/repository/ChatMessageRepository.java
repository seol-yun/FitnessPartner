package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.ChatMessage;
import fitnessapp.fitnesspartner.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
}
