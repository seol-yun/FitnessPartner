package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.config.JwtUtil;
import fitnessapp.fitnesspartner.domain.Member;
//import fitnessapp.fitnesspartner.service.EncryptionService;
//import fitnessapp.fitnesspartner.service.FirewallService;
import fitnessapp.fitnesspartner.service.LoginService;
import fitnessapp.fitnesspartner.service.MemberService;
//import fitnessapp.fitnesspartner.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtUtil jwtUtil;
//    @Autowired
//    private EncryptionService encryptionService;
//    @Autowired
//    private FirewallService firewallService;
//    @Autowired
//    private AuthenticationService authenticationService;
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
        logger.info("로그인 시도: {}", credentials);

        String id = credentials.get("id");
        String pw = credentials.get("pw");

        String token = loginService.login(id, pw);

        if (!token.equals("0")) {
            return ResponseEntity.ok().body(Map.of("token", token));
        } else {
            logger.warn("로그인 실패: {}", credentials);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }

//    @PostMapping("/login")
//    @Operation(summary = "로그인", description = "회원 ID와 비밀번호를 입력받아 로그인을 처리합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = Map.class))),
//            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content(schema = @Schema(implementation = String.class)))
//    })
//    public ResponseEntity<?> login(
//            @Parameter(description = "로그인 정보(ID와 비밀번호)", required = true) @RequestBody Map<String, String> credentials) {
//        logger.info("로그인 시도: {}", credentials);
//
//        SfcPacket packet = new SfcPacket(credentials);
//        packet.setSfp("encryptionService->firewallService->authenticationService->loginService");
//
//        // SFC 체인을 따라 데이터 처리
//        packet = encryptionService.encrypt(packet);
//        packet = firewallService.filter(packet);
//        packet = authenticationService.authenticate(packet);
//
//        logger.info("인증 결과: {}", packet.isAuthenticated());
//
//        // memberService.login을 호출하여 추가 검증 수행
//        String id = credentials.get("id");
//        String pw = credentials.get("pw");
//        String member = memberService.login(id, pw);
//
//        if (!member.equals("0") && packet.isAuthenticated()) {
//            String token = jwtUtil.generateToken(id);
//            return ResponseEntity.ok().body(Map.of("token", token));
//        } else {
//            logger.warn("로그인 실패: {}", credentials);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
//        }
//    }

}









//
//
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
//
//    @Autowired
//    private JwtUtil jwtUtil;
//    @Autowired
//    private EncryptionService encryptionService;
//    @Autowired
//    private FirewallService firewallService;
//    @Autowired
//    private AuthenticationService authenticationService;
//    @Autowired
//    private LoginService loginService;
//    @Autowired
//    private MemberService memberService;
//
//    @PostMapping("/signup")
//    @Operation(summary = "회원가입", description = "회원 정보를 입력받아 회원가입을 처리합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
//            @ApiResponse(responseCode = "400", description = "중복된 회원", content = @Content(schema = @Schema(implementation = String.class)))
//    })
//    public ResponseEntity<String> signup(
//            @Parameter(description = "회원 ID", required = true) @RequestParam("id") String id,
//            @Parameter(description = "비밀번호", required = true) @RequestParam("pw") String pw,
//            @Parameter(description = "이름", required = true) @RequestParam("name") String name,
//            @Parameter(description = "이메일", required = true) @RequestParam("email") String email,
//            @Parameter(description = "주소", required = true) @RequestParam("address") String address,
//            @Parameter(description = "성별", required = true) @RequestParam("gender") String gender,
//            @Parameter(description = "운동 유형", required = true) @RequestParam("exerciseType") String exerciseType,
//            @Parameter(description = "트레이너 여부", required = true) @RequestParam("isTrainer") boolean isTrainer) {
//        Member newMember = new Member(id, pw, name, email, address, gender, exerciseType, isTrainer);
//        String result = loginService.signUp(newMember);
//        if ("회원가입 성공!".equals(result)) {
//            return ResponseEntity.ok(result);
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
//        }
//    }
//
//    @PostMapping("/login")
//    @Operation(summary = "로그인", description = "회원 ID와 비밀번호를 입력받아 로그인을 처리합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = Map.class))),
//            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content(schema = @Schema(implementation = String.class)))
//    })
//    public ResponseEntity<?> login(
//            @Parameter(description = "로그인 정보(ID와 비밀번호)", required = true) @RequestBody Map<String, String> credentials,
//            HttpServletRequest request) {
//        logger.info("로그인 시도: {}", credentials);
//
//        SfcPacket packet = new SfcPacket(credentials);
//        packet.setSfp("encryptionService->firewallService->authenticationService->loginService");
//
//        // 클라이언트 IP 설정
//        String clientIP = request.getRemoteAddr();
//        packet.getData().put("clientIP", clientIP);
//
//        // SFC 체인을 따라 데이터 처리
//        packet = encryptionService.encrypt(packet);
//        packet = firewallService.filter(packet);
//        packet = authenticationService.authenticate(packet);
//
//        logger.info("인증 결과: {}", packet.isAuthenticated());
//
//        // memberService.login을 호출하여 추가 검증 수행
//        String id = credentials.get("id");
//        String pw = credentials.get("pw");
//        String member = memberService.login(id, pw);
//
//        if (!member.equals("0") && packet.isAuthenticated()) {
//            String token = jwtUtil.generateToken(id);
//            return ResponseEntity.ok().body(Map.of("token", token));
//        } else {
//            logger.warn("로그인 실패: {}", credentials);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
//        }
//    }
//}
