package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public String signup(@RequestBody Member member) {
            int validate = memberService.validateDuplicateMember(member);
            if(validate==0){
                return "중복";
            } else {
                memberService.join(member);
                return "회원가입 성공!";
            }
    }
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String id = credentials.get("id");
        String pw = credentials.get("pw");

        String member = memberService.login(id, pw);
        if(!member.equals('0')){
            HttpSession session = request.getSession();
            session.setAttribute("loginId", member);
            session.setMaxInactiveInterval(60 * 30);
            System.out.println(session);
        }
        return member;
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println(session+"!!!!!!!!!!!!!!!!!!!");
            session.invalidate();
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Member> getMemberInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginId") != null) {
            String loginId = (String) session.getAttribute("loginId");
            Member member = memberService.findOne(loginId);
            if (member != null) {
                return ResponseEntity.ok().body(member);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    @GetMapping("/") // 기본 경로에 대한 요청 처리
    public String redirectToLogin() {
        return "/login.html"; // 정적 자원 경로를 포함한 절대 경로
    }

    // 추가적인 RESTful 엔드포인트들을 필요에 따라 정의할 수 있습니다.
}
