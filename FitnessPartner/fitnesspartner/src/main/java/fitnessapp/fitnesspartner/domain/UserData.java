package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
    private int height;
    private int weight;

    @Column(name = "exercise_type") //진행한 운동 종류
    private String exerciseType;
    @Column(name = "duration_minutes") //운동 수행 시간을 분 단위로 표기
    private int durationMinutes;
    @Column(name = "date")  //기록한 날짜
    private LocalDate date;

    protected UserData() {}

    // 필수 필드를 포함한 생성자
    public UserData(Member member, LocalDate date, int height, int weight, String exerciseType, int durationMinutes) {
        this.member = member;
        this.height = height;
        this.weight = weight;
        this.exerciseType = exerciseType;
        this.durationMinutes = durationMinutes;
        this.date = date;
    }
}
