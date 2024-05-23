package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.service.BlockService;
import fitnessapp.fitnesspartner.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BlockService blockService;

    @PostMapping("/signup")
    public String signup(@RequestParam("id") String id, @RequestParam("pw") String pw,
                         @RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("address") String address,
                         @RequestParam("gender") String gender, @RequestParam("exerciseType") String exerciseType, @RequestParam("isTrainer") boolean isTrainer) {
        int validate = memberService.validateDuplicateMember(new Member(id, pw, name, email, address, gender, exerciseType, isTrainer));
        if (validate == 0) {
            return "중복";
        } else {
            // 회원가입 처리
            memberService.join(new Member(id, pw, name, email, address, gender, exerciseType, isTrainer));
            return "회원가입 성공!";
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String id = credentials.get("id");
        String pw = credentials.get("pw");

        String member = memberService.login(id, pw);
        if (!member.equals('0')) {
            HttpSession session = request.getSession();
            session.setAttribute("loginId", member);
            session.setMaxInactiveInterval(60 * 30);
        }
        return member;
    }

    @GetMapping("/profileImage/{id}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String id) {
        // 이미지 파일 경로 설정
        String imagePath = "D://Code//FitnessPartner//FitnessPartner//FitnessPartner//fitnesspartner//src//main//resources//static//image//memberprofile//" + id + ".jpg";

        // 이미지 파일을 Resource로 읽어옴
        Resource imageResource = new FileSystemResource(imagePath);

        // 이미지 파일이 있는지 확인
        if (imageResource.exists() && imageResource.isReadable()) {
            // 이미지 파일을 응답으로 전송
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageResource);
        } else {
            // 이미지 파일이 없으면 에러 응답
            return ResponseEntity.notFound().build();
        }
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

            //차단한 사용자는 제외하고 보여줌.
            List<String> blockMembersIds = blockService.findAllBlockMembers(loginId);
            List<MemberInfo> memberInfos = new ArrayList<>();
            for (Member member : allMembers) {
                if (!blockMembersIds.contains(member.getId())) {
                    memberInfos.add(new MemberInfo(member.getId(), member.getName(), member.getEmail()));
                }
            }

            return ResponseEntity.ok().body(memberInfos);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 이름(name)과 이메일(email)만 가지는 MemberInfo 클래스 정의
    @Setter
    @Getter
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

}
