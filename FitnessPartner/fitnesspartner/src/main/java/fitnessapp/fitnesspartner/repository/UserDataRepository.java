package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.Block;
import fitnessapp.fitnesspartner.domain.UserData;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDataRepository {

    private final EntityManager em;

    public UserData save(UserData userData) {
        em.persist(userData);
        return userData;
    }

}
