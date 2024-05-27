package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.Block;
import fitnessapp.fitnesspartner.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockRepository {

    private final EntityManager em;

    public Block save(Block block) {
        em.persist(block);
        return block;
    }

    public void delete(Block block) {
        em.remove(block);
    }

    public List<Block> findAllByMemberId(String memberId) {
        return em.createQuery("select b from Block b where b.member.id = :memberId", Block.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    /*
        차단 해제 하기 위해서 사용.
     */
    public Block findByMemberAndBlockedMember(String memberId, String blockedMemberId) {
        return em.createQuery("select b from Block b where b.member.id = :memberId and b.blockedMember.id = :blockedMemberId", Block.class)
                .setParameter("memberId", memberId)
                .setParameter("blockedMemberId", blockedMemberId)
                .getSingleResult();
    }

}