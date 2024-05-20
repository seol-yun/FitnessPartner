package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NameController {

//    private final MemberRepository memberRepository;
//
//    public NameController(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
//
//    @PostMapping("/set-name")
//    public Map<String, String> setName(HttpSession session) {
//        Map<String, String> response = new HashMap<>();
//        String senderName = null;
//        String loginId = (String) session.getAttribute("loginId");
//
//        // 로그인 ID를 이용하여 회원 정보 조회
//        if (loginId != null) {
//            Member member = memberRepository.findOne(loginId);
//            if (member != null) {
//                senderName = member.getName(); // 발신자의 이름 설정
//            }
//        }
//
//        response.put("senderName", senderName);
//        return response;
//    }
}
