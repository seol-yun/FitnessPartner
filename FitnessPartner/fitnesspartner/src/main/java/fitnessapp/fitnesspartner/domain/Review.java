package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id; // 고유 ID

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 500)
    private String content; // 내용

    @Column(nullable = false)
    private int rating; // 평점

    @Column(nullable = false, length = 50)
    private String author; // 작성자

    @Column(nullable = false)
    private LocalDateTime createdDate; // 작성 일시

    public Review() {
        this.createdDate = LocalDateTime.now();
    }

    public Review(String content, int rating, String author, Member member) {
        this.content = content;
        this.rating = rating;
        this.author = author;
        this.createdDate = LocalDateTime.now();
        this.member = member;
    }

}
