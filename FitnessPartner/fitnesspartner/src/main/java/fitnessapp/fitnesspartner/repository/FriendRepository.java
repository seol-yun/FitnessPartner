package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.Friend;
import fitnessapp.fitnesspartner.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendRepository {
    private final EntityManager em;

    /**
     * 친구 저장
     * @param friend
     * @return
     */
    public Friend save(Friend friend) {
        em.persist(friend);
        return friend;
    }

    /**
     * 친구 전부 반환
     * @param memberId
     * @return
     */
    public List<Friend> findAllByMemberId(String memberId) {
        return em.createQuery("select f from Friend f where f.member.id = :memberId", Friend.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}