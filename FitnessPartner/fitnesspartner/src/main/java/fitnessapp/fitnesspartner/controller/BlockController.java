package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.dto.BlockInfoDTO;
import fitnessapp.fitnesspartner.service.BlockService;
import fitnessapp.fitnesspartner.config.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
@Tag(name = "Block Controller", description = "차단 관리 API")
public class BlockController {
    private final BlockService blockService;
    private final JwtUtil jwtUtil;

    @PostMapping("/addBlock")
    @Operation(summary = "차단 추가", description = "특정 회원을 차단합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "차단 추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> addBlock(
            @Parameter(description = "차단할 회원의 ID", required = true) @RequestBody AddBlockRequest request,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String memberId = jwtUtil.extractUsername(token);
            blockService.addBlock(memberId, request.getBlockMemberId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/unBlock")
    @Operation(summary = "차단 해제", description = "특정 회원의 차단을 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "차단 해제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> unBlock(
            @Parameter(description = "차단 해제할 회원의 ID", required = true) @RequestBody AddBlockRequest request,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String memberId = jwtUtil.extractUsername(token);
            blockService.unBlock(memberId, request.getBlockMemberId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/all")
    @Operation(summary = "차단 목록 조회", description = "현재 로그인된 회원의 차단 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "차단 목록 반환", content = @Content(schema = @Schema(implementation = BlockInfoDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<BlockInfoDTO>> getAllBlocks(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            String loginId = jwtUtil.extractUsername(token);
            List<BlockInfoDTO> blocks = blockService.getAllBlocks(loginId);
            return ResponseEntity.ok().body(blocks);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Getter
    @Setter
    static class AddBlockRequest {
        @Parameter(description = "차단할 회원의 ID", required = true)
        private String blockMemberId;
    }
}
