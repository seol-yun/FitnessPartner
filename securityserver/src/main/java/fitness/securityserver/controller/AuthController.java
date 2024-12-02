package fitness.securityserver.controller;

import fitness.securityserver.config.EncryptionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EncryptionUtil encryptionUtil; // EncryptionUtil 추가

    @PostMapping("/login")
    @Operation(summary = "보안검사", description = "회원 ID와 비밀번호를 입력받아 로그인을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "필터 통과", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "필터 실패", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> login(
            @Parameter(description = "로그인 정보(ID와 비밀번호)", required = true) @RequestBody Map<String, String> credentials) {

        String id = credentials.get("id");
        String pw = credentials.get("pw");

        try {
            // ID와 비밀번호를 암호화합니다.
            String encryptedId = EncryptionUtil.encrypt(id);
            String encryptedPw = EncryptionUtil.encrypt(pw);

            // 로그인 서버에 요청하기 위한 로그인 정보 생성
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("id", encryptedId);
            loginRequest.put("pw", encryptedPw);

            // 로그인 서버에 요청
            String loginServerUrl = "http://172.18.0.3:8080/api/auth/login";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

            ResponseEntity<?> loginResponse = restTemplate.exchange(loginServerUrl, HttpMethod.POST, requestEntity, Object.class);

            return ResponseEntity.status(loginResponse.getStatusCode()).body(loginResponse.getBody());

        } catch (Exception e) {
            logger.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인서버 오류");
        }
    }

}
