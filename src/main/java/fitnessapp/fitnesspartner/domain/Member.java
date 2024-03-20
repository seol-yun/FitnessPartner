package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(
        name = "USER_SEQ_GENERATOR"
        , sequenceName = "USER_SEQ"
        , initialValue = 1
        , allocationSize = 1
)
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "USER_SEQ_GENERATOR"
    )
    @Column(name="member_id")
    private Long id;
    private String name;
    private String email;
}
