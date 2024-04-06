package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.Friend;
import fitnessapp.fitnesspartner.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendRepository {
    private final EntityManager em;
    public Friend save(Friend friend) {
        em.persist(friend);
        return friend;
    }
}