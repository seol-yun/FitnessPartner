package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.config.JwtUtil;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.dto.UserDataDTO;
import fitnessapp.fitnesspartner.service.MemberService;
import fitnessapp.fitnesspartner.service.UserDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/userdata")
@RequiredArgsConstructor
@Tag(name = "User Data Controller", description = "회원 신체 및 운동 정보 관리 API")
public class UserDataController {

    private final UserDataService userDataService;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @GetMapping("/physicalinfo")
    @Operation(summary = "회원 신체 정보 조회", description = "현재 로그인된 회원의 모든 신체 데이터를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신체 데이터 반환"),
            @ApiResponse(responseCode = "401", description = "로그인되지 않음"),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음")
    })
    public ResponseEntity<List<UserDataDTO>> getMemberPhysicalInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String loginId = jwtUtil.extractUsername(token);

        Member member = memberService.findOne(loginId);
        if (member != null) {
            List<UserDataDTO> physicalInfo = userDataService.getAllPhysicalDataByMemberId(member.getId());
            return ResponseEntity.ok().body(physicalInfo);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
