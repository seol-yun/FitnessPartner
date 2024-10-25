package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    private String id;
    private String pw;
    private String name;
    private String email;
    private String address;
    private String gender;
    private String exerciseType;
    private boolean isTrainer;


    public Member() {
    }

    public Member(String id, String pw, String name, String email, String address, String gender, String exerciseType, boolean isTrainer) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.exerciseType = exerciseType;
        this.isTrainer = isTrainer;
    }

}
