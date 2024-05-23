package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.domain.Block;
import fitnessapp.fitnesspartner.domain.Friend;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.dto.BlockInfoDTO;
import fitnessapp.fitnesspartner.repository.BlockRepository;
import fitnessapp.fitnesspartner.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;

    public void addBlock(String memberId, String blockId) {
        // memberId와 blockId에 해당하는 Member 엔티티 가져오기
        Member member = memberRepository.findOne(memberId);
        Member blockMember = memberRepository.findOne(blockId);

        // Block 엔티티 생성
        Block newBlock = new Block();
        newBlock.setMember(member);
        newBlock.setBlockedMember(blockMember);

        // Block 엔티티 저장
        blockRepository.save(newBlock);
    }

    public void unBlock(String memberId, String blockId) {
        // memberId와 blockId에 해당하는 Member 엔티티 가져오기
        Member member = memberRepository.findOne(memberId);
        Member blockedMember = memberRepository.findOne(blockId);

        Block block = blockRepository.findByMemberAndBlockedMember(member.getId(), blockedMember.getId());

        if (block != null) {
            // 차단된 회원 정보를 삭제하고 차단 해제
            blockRepository.delete(block);
        } else {
            // 차단된 회원이 없는 경우에 대한 예외 처리 또는 메시지 출력
            System.out.println("해당 회원이 차단되어 있지 않습니다.");
        }
    }

    public List<BlockInfoDTO> getAllBlocks(String memberId) {
        return blockRepository.findAllByMemberId(memberId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BlockInfoDTO convertToDTO(Block block) {
        BlockInfoDTO dto = new BlockInfoDTO();
        dto.setBlockedName(block.getBlockedMember().getName());
        dto.setBlockId(block.getBlockedMember().getId());
        return dto;
    }

    public List<String> findAllBlockMembers(String loginId) {
        // 로그인한 사용자의 차단 목록 조회
        List<Block> blocks = blockRepository.findAllByMemberId(loginId);
        List<String> blockIds = blocks.stream()
                .map(block -> block.getBlockedMember().getId())
                .collect(Collectors.toList());

        // TODO 차단한 사용자는 제외


//        // 모든 회원 정보 조회 후 로그인한 사용자와 차단인 회원을 필터링
//        List<Member> allMembers = memberRepository.findAll();
//        return allMembers.stream()
//                .filter(member -> !member.getId().equals(loginId) && !blockIds.contains(member.getId()))
//                .collect(Collectors.toList());
        return blockIds;
    }

}
