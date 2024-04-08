package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id
    @Column(name="member_id")
    private String id;
    private String pw;
    private String name;
    private String email;

    public Member() {
    }
    public Member(String id, String pw, String name, String email) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.email = email;
    }
}
