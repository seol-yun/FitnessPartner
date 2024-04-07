package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.domain.Friend;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.repository.FriendRepository;
import fitnessapp.fitnesspartner.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor //final만 사용해서 생성자 만듦(lombok)
public class MemberService {
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    /**
     * 회원 가입
     */
    public String join(Member member){
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    public int validateDuplicateMember(Member member) {
        Member existingMember = memberRepository.findOne(member.getId());
        if (existingMember != null) {
            return 0;
        } else{
            return 1;
        }
    }
    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(String memberId){
        return memberRepository.findOne(memberId);
    }

    /**
     * 로그인
     */
    public String login(String memberId, String password) {
        // 아이디로 회원을 찾기
        Member member = memberRepository.findOne(memberId);
        if (member != null) {
            // 비밀번호가 일치하면 로그인 성공 메시지 반환
            if (member.getPw().equals(password)) {
                return member.getId();
            } else {
                // 비밀번호가 일치하지 않으면 실패 메시지 반환
                return "0";
            }
        } else {
            // 회원이 존재하지 않으면 실패 메시지 반환
            return "0";
        }
    }

    /**
     * 본인, 친구 제외한 멤버 출력
     */
    public List<Member> findAllExcept(String loginId) {
        // 로그인한 사용자의 친구 목록 조회
        List<Friend> friends = friendRepository.findAllByMemberId(loginId);
        List<String> friendIds = friends.stream()
                .map(friend -> friend.getFriendMember().getId())
                .collect(Collectors.toList());

        // 모든 회원 정보 조회 후 로그인한 사용자와 친구인 회원을 필터링
        List<Member> allMembers = memberRepository.findAll();
        return allMembers.stream()
                .filter(member -> !member.getId().equals(loginId) && !friendIds.contains(member.getId()))
                .collect(Collectors.toList());
    }

}
