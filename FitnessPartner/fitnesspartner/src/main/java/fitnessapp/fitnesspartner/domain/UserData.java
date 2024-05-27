package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class UserData {

    @Id
    @GeneratedValue
    @Column(name = "userData_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int height; //키
    private int weight; //몸무게

    private String exerciseType; //진행한 운동 종류
    private int durationMinutes; //운동 수행 시간을 분 단위로 표기
    private LocalDate createdDate; //신체 정보면 등록한 날짜
                                   //운동 정보면 운동을 진행한 날짜

    protected UserData() {}

    // 필수 필드를 포함한 생성자
    public UserData(Member member, LocalDate date, int height, int weight, String exerciseType, int durationMinutes) {
        this.member = member;
        this.height = height;
        this.weight = weight;
        this.exerciseType = exerciseType;
        this.durationMinutes = durationMinutes;
        this.createdDate = date;
    }
}
