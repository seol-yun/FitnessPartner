package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.ExerciseData;
import fitnessapp.fitnesspartner.domain.UserData;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExerciseDataRepository {

    private final EntityManager em;

    public ExerciseData save(ExerciseData exerciseData) {
        em.persist(exerciseData);
        return exerciseData;
    }
}
