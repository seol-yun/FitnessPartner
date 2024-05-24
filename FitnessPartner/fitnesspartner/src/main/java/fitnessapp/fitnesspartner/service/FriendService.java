package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.domain.Friend;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.dto.FriendInfoDTO;
import fitnessapp.fitnesspartner.repository.FriendRepository;
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
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    public void addFriend(String memberId, String friendId) {
        // memberId와 friendId에 해당하는 Member 엔티티 가져오기
        Member member = memberRepository.findOne(memberId);
        Member friend = memberRepository.findOne(friendId);

        // Friend 엔티티 생성
        Friend newFriendship = new Friend();
        newFriendship.setMember(member);
        newFriendship.setFriendMember(friend);

        // Friend 엔티티 저장
        friendRepository.save(newFriendship);
    }

    public void deleteFriend(String memberId, String friendId) {
        Friend friend = friendRepository.findByMemberIdAndFriendId(memberId, friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend relationship not found"));
        friendRepository.delete(friend);
    }

    public List<FriendInfoDTO> getAllFriends(String memberId) {
        return friendRepository.findAllByMemberId(memberId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FriendInfoDTO convertToDTO(Friend friend) {
        FriendInfoDTO dto = new FriendInfoDTO();
        dto.setFriendName(friend.getFriendMember().getName());
        dto.setFriendId(friend.getFriendMember().getId());
        return dto;
    }
}
