package fitnessapp.fitnesspartner.repository;

import fitnessapp.fitnesspartner.domain.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {

    private final EntityManager em;

    public Review save(Review review) {
        if (review.getId() == null) {
            em.persist(review);
        } else {
            em.merge(review);
        }
        return review;
    }

    public void delete(Review review) {
        em.remove(review);
    }

    public Optional<Review> findById(Long id) {
        Review review = em.find(Review.class, id);
        return review != null ? Optional.of(review) : Optional.empty();
    }

    public List<Review> findAll() {
        TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r", Review.class);
        return query.getResultList();
    }

    public List<Review> findByAuthor(String author) {
        TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r WHERE r.author = :author", Review.class);
        query.setParameter("author", author);
        return query.getResultList();
    }

    public List<Review> findByMemberId(Long memberId) {
        TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r WHERE r.member.id = :memberId", Review.class);
        query.setParameter("memberId", memberId);
        return query.getResultList();
    }
}
