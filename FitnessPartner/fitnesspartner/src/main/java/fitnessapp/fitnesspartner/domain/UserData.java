package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class UserData {

    @Id
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
    @Column(name = "date")  //운동 수행 날짜
    private LocalDateTime date;

}
