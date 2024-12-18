package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.config.JwtUtil;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.service.BlockService;
import fitnessapp.fitnesspartner.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member Controller", description = "회원 관리 API")
public class MemberController {

    private final MemberService memberService;
    private final BlockService blockService;
    private final JwtUtil jwtUtil;

    @GetMapping("/profileImage/{id}")
    @Operation(summary = "프로필 이미지 가져오기", description = "회원 ID를 입력받아 해당 회원의 프로필 이미지를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 반환", content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "404", description = "이미지 없음")
    })
    public ResponseEntity<Resource> getProfileImage(
            @Parameter(description = "회원 ID", required = true) @PathVariable String id) {
        String imagePath = "/app/FitnessImage/" + id + ".jpg";

        Resource imageResource = new FileSystemResource(imagePath);

        if (imageResource.exists() && imageResource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/uploadProfileImage")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("id") String id,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("빈 파일입니다.");
            }

            // 도커 컨테이너 내부 경로
            String directoryPath = "/app/FitnessImage";
            Path directory = Paths.get(directoryPath);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory); // 경로가 없을 경우 생성
            }

            // 파일 저장 경로
            Path filePath = directory.resolve(id + ".jpg");
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("이미지 업로드 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패: " + e.getMessage());
        }
    }


    @GetMapping("/info")
    @Operation(summary = "회원 정보 조회", description = "현재 로그인된 회원의 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 반환", content = @Content(schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "401", description = "로그인되지 않음"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음")
    })
    public ResponseEntity<Member> getMemberInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtUtil.extractUsername(token);

        Member member = memberService.findOne(loginId);
        if (member != null) {
            return ResponseEntity.ok().body(member);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    @Operation(summary = "모든 회원 정보 조회", description = "로그인된 회원을 제외한 모든 회원 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 목록 반환", content = @Content(schema = @Schema(implementation = MemberInfo.class))),
            @ApiResponse(responseCode = "401", description = "로그인되지 않음")
    })
    public ResponseEntity<List<MemberInfo>> getAllMembers(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtUtil.extractUsername(token);

        List<Member> allMembers = memberService.findAllExcept(loginId);

        List<String> blockMembersIds = blockService.findAllBlockMembers(loginId);
        List<MemberInfo> memberInfos = new ArrayList<>();
        for (Member member : allMembers) {
            if (!blockMembersIds.contains(member.getId())) {
                memberInfos.add(new MemberInfo(member.getId(), member.getName(), member.getExerciseType(), member.getGender(), member.getAddress()));
            }
        }

        return ResponseEntity.ok().body(memberInfos);
    }

    @GetMapping("/generalUsers")
    @Operation(summary = "일반 회원 정보 조회", description = "로그인된 회원을 제외한 모든 일반 회원 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일반 회원 목록 반환", content = @Content(schema = @Schema(implementation = MemberInfo.class))),
            @ApiResponse(responseCode = "401", description = "로그인되지 않음")
    })
    public ResponseEntity<List<MemberInfo>> getGeneralMembers(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtUtil.extractUsername(token);

        List<Member> allMembers = memberService.findGeneralMembers(loginId);

        List<String> blockMembersIds = blockService.findAllBlockMembers(loginId);
        List<MemberInfo> memberInfos = new ArrayList<>();
        for (Member member : allMembers) {
            if (!blockMembersIds.contains(member.getId())) {
                memberInfos.add(new MemberInfo(member.getId(), member.getName(), member.getExerciseType(), member.getGender(), member.getAddress()));
            }
        }

        return ResponseEntity.ok().body(memberInfos);
    }

    @GetMapping("/trainerUsers")
    @Operation(summary = "트레이너 회원 정보 조회", description = "로그인된 회원을 제외한 모든 트레이너 회원 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "트레이너 회원 목록 반환", content = @Content(schema = @Schema(implementation = MemberInfo.class))),
            @ApiResponse(responseCode = "401", description = "로그인되지 않음")
    })
    public ResponseEntity<List<MemberInfo>> getTrainerMembers(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtUtil.extractUsername(token);

        List<Member> allMembers = memberService.findTrainerMembers(loginId);

        List<String> blockMembersIds = blockService.findAllBlockMembers(loginId);
        List<MemberInfo> memberInfos = new ArrayList<>();
        for (Member member : allMembers) {
            if (!blockMembersIds.contains(member.getId())) {
                memberInfos.add(new MemberInfo(member.getId(), member.getName(), member.getExerciseType(), member.getGender(), member.getAddress()));
            }
        }

        return ResponseEntity.ok().body(memberInfos);
    }

    @PostMapping("/addPhysicalInfo")
    @Operation(summary = "신체 정보 추가", description = "로그인된 회원의 신체 정보를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신체 정보 추가 성공"),
            @ApiResponse(responseCode = "401", description = "로그인되지 않음")
    })
    public String addPhysicalInfo(
            @Parameter(description = "날짜", required = true) @RequestParam("date") String date,
            @Parameter(description = "키", required = true) @RequestParam("height") String height,
            @Parameter(description = "몸무게", required = true) @RequestParam("weight") String weight,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtUtil.extractUsername(token);

        memberService.addPhysicalData(loginId, date, height, weight);
        return "신체정보추가 성공!";
    }

    @PostMapping("/addExerciseInfo")
    @Operation(summary = "운동 정보 추가", description = "로그인된 회원의 운동 정보를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "운동 정보 추가 성공"),
            @ApiResponse(responseCode = "401", description = "로그인되지 않음")
    })
    public String addExerciseInfo(
            @Parameter(description = "날짜", required = true) @RequestParam("date") String date,
            @Parameter(description = "운동 유형", required = true) @RequestParam("exerciseType") String exerciseType,
            @Parameter(description = "운동 시간(분)", required = true) @RequestParam("durationMinutes") String durationMinutes,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtUtil.extractUsername(token);

        memberService.addExerciseData(loginId, date, exerciseType, durationMinutes);
        return "운동정보추가 성공!";
    }

    @PostMapping("/update")
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정하고 프로필 이미지를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "수정 실패", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public String update(
            @Parameter(description = "비밀번호", required = true) @RequestParam("pw") String pw,
            @Parameter(description = "이름", required = true) @RequestParam("name") String name,
            @Parameter(description = "이메일", required = true) @RequestParam("email") String email,
            @Parameter(description = "주소", required = true) @RequestParam("address") String address,
            @Parameter(description = "성별", required = true) @RequestParam("gender") String gender,
            @Parameter(description = "운동 유형", required = true) @RequestParam("exerciseType") String exerciseType,
            @Parameter(description = "트레이너 여부", required = true) @RequestParam("isTrainer") boolean isTrainer,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtUtil.extractUsername(token);
        memberService.update(new Member(loginId, pw, name, email, address, gender, exerciseType, isTrainer));
        return "수정 성공!";
    }

    @Setter
    @Getter
    class MemberInfo {
        private String id;
        private String name;
        private String exerciseType;
        private String gender;
        private String address;

        public MemberInfo(String id, String name, String exerciseType, String gender, String address) {
            this.id = id;
            this.name = name;
            this.exerciseType = exerciseType;
            this.gender = gender;
            this.address = address;
        }
    }
}
