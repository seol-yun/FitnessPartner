package fitnessapp.fitnesspartner.controller;

import fitnessapp.fitnesspartner.domain.Block;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.dto.BlockInfoDTO;
import fitnessapp.fitnesspartner.service.BlockService;
import fitnessapp.fitnesspartner.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {
    private final BlockService blockService;

    @PostMapping("/addBlock")
    public void addBlock(@RequestBody AddBlockRequest request) {
        blockService.addBlock(request.getMemberId(), request.getBlockMemberId());
    }

    @PostMapping("/unBlock")
    public void unBlock(@RequestBody AddBlockRequest request) {
        blockService.unBlock(request.getMemberId(), request.getBlockMemberId());
    }

    @GetMapping("/all")
    public ResponseEntity<List<BlockInfoDTO>> getAllBlocks(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginId") != null) {
            String loginId = (String) session.getAttribute("loginId");
            List<BlockInfoDTO> blocks = blockService.getAllBlocks(loginId);
            return ResponseEntity.ok().body(blocks);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Getter
    @Setter
    static class AddBlockRequest {
        private String memberId;
        private String blockMemberId;
    }
}

