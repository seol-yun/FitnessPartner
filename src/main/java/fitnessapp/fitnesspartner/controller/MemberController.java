package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     */
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("member", new Member());
        return "signupForm";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute Member member) {
        memberService.join(member);
        return "redirect:/login"; // 회원가입 후 로그인 페이지로 이동
    }

    /**
     *로그인
     */
    @GetMapping("/login")
    public String loginForm() {
        return "loginForm";
    }

    @PostMapping("/login")
    public String login(String id, String password) {
        String result = memberService.login(id, password);
        if (result.startsWith("로그인 성공")) {
            // 로그인 성공 시 회원 정보 페이지로 리디렉션
            return "redirect:/memberInfo?id=" + id;
        } else {
            // 로그인 실패 시 다시 로그인 페이지로 이동
            return "redirect:/login";
        }
    }

    // 로그인 기능은 실제 사용자 인증과 관련된 코드가 필요하므로 여기서는 생략합니다.
    // 실제로는 Spring Security나 직접 인증 처리를 구현해야 합니다.

    /**
     * 회원정보 페이지
     */
    @GetMapping("/memberInfo")
    public String showMemberInfo(@RequestParam("id") String memberId, Model model) {
        // 회원 정보를 조회하여 모델에 추가
        Member member = memberService.findOne(memberId);
        model.addAttribute("memberId", member.getId());
        model.addAttribute("memberName", member.getName());
        model.addAttribute("memberEmail", member.getEmail());
        // 추가적인 회원 정보를 필요에 따라 모델에 추가

        // memberInfo.html을 렌더링하도록 리턴
        return "memberInfo";
    }
}