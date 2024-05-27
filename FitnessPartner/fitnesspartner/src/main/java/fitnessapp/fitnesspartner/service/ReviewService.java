package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.domain.Review;
import fitnessapp.fitnesspartner.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public void deleteById(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        review.ifPresent(reviewRepository::delete);
    }

    public List<Review> findByAuthor(String author) {
        return reviewRepository.findByAuthor(author);
    }

    public List<Review> findByMemberId(Long memberId) {
        return reviewRepository.findByMemberId(memberId);
    }
}
