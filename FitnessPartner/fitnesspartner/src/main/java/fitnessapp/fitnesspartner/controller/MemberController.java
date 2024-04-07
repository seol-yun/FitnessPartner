package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
        }
        return member;
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
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


    @GetMapping("/all")
    public ResponseEntity<List<MemberInfo>> getAllMembers(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginId") != null) {
            String loginId = (String) session.getAttribute("loginId");
            List<Member> allMembers = memberService.findAllExcept(loginId);

            // 이름과 이메일만을 가지는 MemberInfo 객체 리스트 생성
            List<MemberInfo> memberInfos = new ArrayList<>();
            for (Member member : allMembers) {
                memberInfos.add(new MemberInfo(member.getId(), member.getName(), member.getEmail()));
            }
            return ResponseEntity.ok().body(memberInfos);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 이름(name)과 이메일(email)만 가지는 MemberInfo 클래스 정의
    @Setter @Getter
    class MemberInfo {
        private String id;
        private String name;
        private String email;
        // 생성자, Getter, Setter
        public MemberInfo(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }


    @GetMapping("/") // 기본 경로에 대한 요청 처리
    public String redirectToLogin() {
        return "/login.html"; // 정적 자원 경로를 포함한 절대 경로
    }

    // 추가적인 RESTful 엔드포인트들을 필요에 따라 정의할 수 있습니다.
}
