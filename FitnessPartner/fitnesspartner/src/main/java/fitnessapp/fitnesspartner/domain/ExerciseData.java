package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ExerciseData {
    @Id
    @GeneratedValue
    @Column(name = "exerciseData_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "exercise_type") //진행한 운동 종류
    private String exerciseType;
    @Column(name = "duration_minutes") //운동 수행 시간을 분 단위로 표기
    private int durationMinutes;
    @Column(name = "date")  //기록한 날짜
    private LocalDate date;

    protected ExerciseData() {}

    // 필수 필드를 포함한 생성자
    public ExerciseData(Member member, LocalDate date, String exerciseType, int durationMinutes) {
        this.member = member;
        this.exerciseType = exerciseType;
        this.durationMinutes = durationMinutes;
        this.date = date;
    }

}
