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

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {
    private final BlockService blockService;
    private final JwtUtil jwtUtil;

    @PostMapping("/addBlock")
    public ResponseEntity<Void> addBlock(@RequestBody AddBlockRequest request, HttpServletRequest httpRequest) {
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
    public ResponseEntity<Void> unBlock(@RequestBody AddBlockRequest request, HttpServletRequest httpRequest) {
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
        private String blockMemberId;
    }
}
