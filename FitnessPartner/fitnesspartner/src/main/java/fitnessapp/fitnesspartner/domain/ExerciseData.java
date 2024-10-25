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
    @Column(name = "EXERCISE_DATA_ID") // 대문자로 변경
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID") // 대문자로 변경
    private Member member;

    @Column(name = "EXERCISE_TYPE") // 대문자로 변경
    private String exerciseType;

    @Column(name = "DURATION_MINUTES") // 대문자로 변경
    private int durationMinutes;

    @Column(name = "Exercise_DATE") // 대문자로 변경
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
