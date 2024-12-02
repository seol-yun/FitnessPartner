package loginserver.loginserver.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @Column(name = "member_id")
    private String id;
    private String pw;
    private String name;
    private String email;
    private String address;
    private String gender;
    private String exerciseType;
    private boolean isTrainer;

//    @OneToMany(mappedBy = "member")
//    private List<Review> reviews;
//
//    @OneToMany(mappedBy = "member")
//    private List<UserData> userDatas;

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
