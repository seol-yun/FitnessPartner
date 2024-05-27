package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.Review;
import fitnessapp.fitnesspartner.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 모든 리뷰를 조회
     *
     * @return 모든 리뷰의 리스트
     */
    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.findAll();
    }

    /**
     * ID로 리뷰를 조회
     *
     * @param id 리뷰의 ID
     * @return 해당 ID의 리뷰
     */
    @GetMapping("/{id}")
    public Optional<Review> getReviewById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    /**
     * 새로운 리뷰를 생성
     *
     * @param review 생성할 리뷰 정보
     * @return 생성된 리뷰
     */
    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.save(review);
    }

    /**
     * ID로 리뷰를 업데이트
     *
     * @param id            리뷰의 ID
     * @param reviewDetails 업데이트할 리뷰 정보
     * @return 업데이트된 리뷰
     */
    @PutMapping("/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review reviewDetails) {
        Optional<Review> reviewOpt = reviewService.findById(id);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            review.setContent(reviewDetails.getContent());
            review.setRating(reviewDetails.getRating());
            review.setAuthor(reviewDetails.getAuthor());
            review.setMember(reviewDetails.getMember());
            return reviewService.save(review);
        } else {
            throw new IllegalArgumentException("Review not found");
        }
    }

    /**
     * ID로 리뷰를 삭제합니다.
     *
     * @param id 삭제할 리뷰의 ID
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id);
    }

    /**
     * 작성자로 리뷰를 조회합니다.
     *
     * @param author 작성자 이름
     * @return 해당 작성자의 리뷰 리스트
     */
    @GetMapping("/author/{author}")
    public List<Review> getReviewsByAuthor(@PathVariable String author) {
        return reviewService.findByAuthor(author);
    }

    /**
     * 회원 ID로 리뷰를 조회합니다.
     *
     * @param memberId 회원의 ID
     * @return 해당 회원의 리뷰 리스트
     */
    @GetMapping("/member/{memberId}")
    public List<Review> getReviewsByMemberId(@PathVariable Long memberId) {
        return reviewService.findByMemberId(memberId);
    }
}
