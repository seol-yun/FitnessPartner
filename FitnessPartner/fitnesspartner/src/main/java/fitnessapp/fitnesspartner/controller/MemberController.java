package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.domain.UserData;
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
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
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
    public String signup(@RequestParam("id") String id, @RequestParam("pw") String pw, @RequestParam("name") String name,
                         @RequestParam("email") String email, @RequestParam("address") String address, @RequestParam("gender") String gender,
                         @RequestParam("exerciseType") String exerciseType, @RequestParam("isTrainer") boolean isTrainer) {
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
        String imagePath = "src/main/resources/static//image/memberprofile/" + id + ".jpg";

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
                    memberInfos.add(new MemberInfo(member.getId(), member.getName(), member.getExerciseType(), member.getGender()));
                }
            }

            return ResponseEntity.ok().body(memberInfos);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/generalUsers")
    public ResponseEntity<List<MemberInfo>> getGeneralMembers(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginId") != null) {
            String loginId = (String) session.getAttribute("loginId");
            List<Member> allMembers = memberService.findGeneralMembers(loginId);

            //차단한 사용자는 제외하고 보여줌.
            List<String> blockMembersIds = blockService.findAllBlockMembers(loginId);
            List<MemberInfo> memberInfos = new ArrayList<>();
            for (Member member : allMembers) {
                if (!blockMembersIds.contains(member.getId())) {
                    memberInfos.add(new MemberInfo(member.getId(), member.getName(), member.getExerciseType(), member.getGender()));
                }
            }

            return ResponseEntity.ok().body(memberInfos);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/trainerUsers")
    public ResponseEntity<List<MemberInfo>> getTrainerMembers(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginId") != null) {
            String loginId = (String) session.getAttribute("loginId");
            List<Member> allMembers = memberService.findTrainerMembers(loginId);

            //차단한 사용자는 제외하고 보여줌.
            List<String> blockMembersIds = blockService.findAllBlockMembers(loginId);
            List<MemberInfo> memberInfos = new ArrayList<>();
            for (Member member : allMembers) {
                if (!blockMembersIds.contains(member.getId())) {
                    memberInfos.add(new MemberInfo(member.getId(), member.getName(), member.getExerciseType(), member.getGender()));
                }
            }

            return ResponseEntity.ok().body(memberInfos);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/addPhysicalInfo")
    public String addPhysicalInfo(@RequestParam("date") String date, @RequestParam("height") String height,
                                  @RequestParam("weight") String weight, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null && session.getAttribute("loginId") == null) {
            return "오류";
        }
        String loginId = (String) session.getAttribute("loginId");

        memberService.addPhysicalData(loginId, date, height, weight);
        return "신체정보추가 성공!";
    }

    @PostMapping("/addExerciseInfo")
    public String addExerciseInfo(@RequestParam("date") String date, @RequestParam("exerciseType") String exerciseType,
                                  @RequestParam("durationMinutes") String durationMinutes, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null && session.getAttribute("loginId") == null) {
            return "오류";
        }
        String loginId = (String) session.getAttribute("loginId");

        memberService.addExerciseData(loginId, date, exerciseType, durationMinutes);
        return "운동정보추가 성공!";
    }

    @PostMapping("/update")
    public String update(@RequestParam("id") String id, @RequestParam("pw") String pw, @RequestParam("name") String name,
                         @RequestParam("email") String email, @RequestParam("address") String address, @RequestParam("gender") String gender,
                         @RequestParam("exerciseType") String exerciseType, @RequestParam("isTrainer") boolean isTrainer,
                         @RequestParam("profilePic") MultipartFile profilePic) {

        try {
            // 프로필 이미지를 저장할 경로 설정
            String uploadDir = "src/main/resources/static/image/memberprofile";
            String fileName = id + ".jpg";

            // 프로필 이미지를 서버에 저장
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);

            // 파일 복사 시 기존 파일 덮어쓰기
            Files.copy(profilePic.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 사용자 정보 업데이트
            memberService.update(new Member(id, pw, name, email, address, gender, exerciseType, isTrainer));
            return "수정 성공!";
        } catch (Exception e) {
            e.printStackTrace();
            return "수정 실패!";
        }
    }

    // 이름(name)과 이메일(email)만 가지는 MemberInfo 클래스 정의
    @Setter
    @Getter
    class MemberInfo {
        private String id;
        private String name;
        private String exerciseType;
        private String gender;

        // 생성자, Getter, Setter
        public MemberInfo(String id, String name, String exerciseType, String gender) {
            this.id = id;
            this.name = name;
            this.exerciseType = exerciseType;
            this.gender = gender;
        }
    }

}
