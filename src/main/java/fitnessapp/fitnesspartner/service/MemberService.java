package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor //final만 사용해서 생성자 만듦(lombok)
public class MemberService {
    private final MemberRepository memberRepository;
    /**
     * 회원 가입
     */
    public String join(Member member){
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
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
                return "로그인 성공! 회원 ID: " + member.getId();
            } else {
                // 비밀번호가 일치하지 않으면 실패 메시지 반환
                return "비밀번호가 일치하지 않습니다.";
            }
        } else {
            // 회원이 존재하지 않으면 실패 메시지 반환
            return "해당 아이디로 가입된 회원이 없습니다.";
        }
    }
}
