package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.Friend;
import fitnessapp.fitnesspartner.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * 특정 친구 관계 반환
     * @param memberId
     * @param friendId
     * @return
     */
    public Optional<Friend> findByMemberIdAndFriendId(String memberId, String friendId) {
        return em.createQuery("select f from Friend f where f.member.id = :memberId and f.friendMember.id = :friendId", Friend.class)
                .setParameter("memberId", memberId)
                .setParameter("friendId", friendId)
                .getResultList()
                .stream()
                .findFirst();
    }

    /**
     * 친구 삭제
     * @param friend
     */
    public void delete(Friend friend) {
        em.remove(friend);
    }
}