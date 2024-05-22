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

    public List<Block> findAllByMemberId(String memberId) {
        return em.createQuery("select b from Block b where b.member.id = :memberId", Block.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}