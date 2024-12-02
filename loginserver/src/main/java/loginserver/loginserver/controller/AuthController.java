package loginserver.loginserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import loginserver.loginserver.config.JwtUtil;
import loginserver.loginserver.domain.Member;
import loginserver.loginserver.service.LoginService;
import loginserver.loginserver.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginService loginService;
    @Autowired
    private MemberService memberService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원 정보를 입력받아 회원가입을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "중복된 회원", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @Transactional
    public ResponseEntity<String> signup(
            @Parameter(description = "회원 ID", required = true) @RequestParam("id") String id,
            @Parameter(description = "비밀번호", required = true) @RequestParam("pw") String pw,
            @Parameter(description = "이름", required = true) @RequestParam("name") String name,
            @Parameter(description = "이메일", required = true) @RequestParam("email") String email,
            @Parameter(description = "주소", required = true) @RequestParam("address") String address,
            @Parameter(description = "성별", required = true) @RequestParam("gender") String gender,
            @Parameter(description = "운동 유형", required = true) @RequestParam("exerciseType") String exerciseType,
            @Parameter(description = "트레이너 여부", required = true) @RequestParam("isTrainer") boolean isTrainer) {
        Member newMember = new Member(id, pw, name, email, address, gender, exerciseType, isTrainer);
        String result = loginService.signUp(newMember);
        if ("회원가입 성공!".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "회원 ID와 비밀번호를 입력받아 로그인을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> login(
            @Parameter(description = "로그인 정보(ID와 비밀번호)", required = true) @RequestBody Map<String, String> credentials) {

        try {
            // 암호화된 ID와 비밀번호 복호화
            String encryptedId = credentials.get("id");
            String encryptedPw = credentials.get("pw");
            String id = loginserver.loginserver.config.EncryptionUtil.decrypt(encryptedId);
            String pw = loginserver.loginserver.config.EncryptionUtil.decrypt(encryptedPw);
            logger.info("로그인 시도: {}", credentials);
            // 로그인 서비스에서 ID와 비밀번호 확인
            String token = loginService.login(id, pw);

            if (!token.equals("0")) {
                return ResponseEntity.ok().body(Map.of("token", token));
            } else {
                logger.warn("로그인 실패: {}", credentials);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
            }
        } catch (Exception e) {
            logger.error("복호화 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 세션을 무효화하여 로그아웃을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    public void logout() {
        // No need to handle logout on the server side with JWT
    }
}








