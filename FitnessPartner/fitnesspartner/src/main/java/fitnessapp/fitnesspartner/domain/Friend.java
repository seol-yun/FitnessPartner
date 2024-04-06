package fitnessapp.fitnesspartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter @Setter
public class Friend {

    @Id
    @GeneratedValue
    @Column(name = "friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_member_id")
    private Member friendMember;

    // 생성 메소드
//    public static Friend createFriend(Member member, Member friendMember) {
//        Friend friend = new Friend();
//        friend.setMember(member);
//        friend.setFriendMember(friendMember);
//        return friend;
//    }
}
