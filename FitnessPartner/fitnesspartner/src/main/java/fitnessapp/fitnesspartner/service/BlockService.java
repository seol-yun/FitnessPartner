package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.domain.Block;
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

    public void addBlcok(String memberId, String blockId) {
        // memberId와 blockId에 해당하는 Member 엔티티 가져오기
        Member member = memberRepository.findOne(memberId);
        Member blockedMember = memberRepository.findOne(blockId);

        // Block 엔티티 생성
        Block newBlock = new Block();
        newBlock.setMember(member);
        newBlock.setBlockedMember(blockedMember);

        // Block 엔티티 저장
        blockRepository.save(newBlock);
    }

    public List<BlockInfoDTO> getAllFriends(String memberId) {
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

}
