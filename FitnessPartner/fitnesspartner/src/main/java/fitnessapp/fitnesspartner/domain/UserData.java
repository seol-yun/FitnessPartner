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
    @Column(name = "USERDATA_ID") // 대문자로 변경
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID") // 대문자로 변경
    private Member member;

    @Column(name = "HEIGHT") // 대문자로 변경
    private int height;

    @Column(name = "WEIGHT") // 대문자로 변경
    private int weight;

    @Column(name = "User_DATE")  // 대문자로 변경
    private LocalDate date;

    protected UserData() {}

    // 필수 필드를 포함한 생성자
    public UserData(Member member, LocalDate date, int height, int weight) {
        this.member = member;
        this.height = height;
        this.weight = weight;
        this.date = date;
    }
}
