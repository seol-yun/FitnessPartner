package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.UserData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserDataRepository {

    private final EntityManager em;

    public UserData save(UserData userData) {
        em.persist(userData);
        return userData;
    }

    public List<UserData> findAllPhysicalDataByMemberId(String memberId) {
        TypedQuery<UserData> query = em.createQuery(
                "SELECT u FROM UserData u WHERE u.member.id = :memberId", UserData.class);
        query.setParameter("memberId", memberId);
        return query.getResultList();
    }
}
