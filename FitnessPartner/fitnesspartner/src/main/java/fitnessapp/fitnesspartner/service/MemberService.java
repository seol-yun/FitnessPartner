package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.domain.ExerciseData;
import fitnessapp.fitnesspartner.domain.Friend;
import fitnessapp.fitnesspartner.domain.Member;
import fitnessapp.fitnesspartner.domain.UserData;
import fitnessapp.fitnesspartner.repository.ExerciseDataRepository;
import fitnessapp.fitnesspartner.repository.FriendRepository;
import fitnessapp.fitnesspartner.repository.MemberRepository;
import fitnessapp.fitnesspartner.repository.UserDataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor //final만 사용해서 생성자 만듦(lombok)
public class MemberService {
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final UserDataRepository userDataRepository;
    private final ExerciseDataRepository exerciseDataRepository;

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(String memberId) {
        return memberRepository.findOne(memberId);
    }


    /**
     * 본인, 친구 제외한 멤버 출력
     */
    public List<Member> findAllExcept(String loginId) {
        // 로그인한 사용자의 친구 목록 조회
        List<Friend> friends = friendRepository.findAllByMemberId(loginId);
        List<String> friendIds = friends.stream()
                .map(friend -> friend.getFriendMember().getId())
                .collect(Collectors.toList());

        // 모든 회원 정보 조회 후 로그인한 사용자와 친구인 회원을 필터링
        List<Member> allMembers = memberRepository.findAll();
        return allMembers.stream()
                .filter(member -> !member.getId().equals(loginId) && !friendIds.contains(member.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 본인, 친구 제외한 일반유저 출력
     */
    public List<Member> findGeneralMembers(String loginId) {
        // 로그인한 사용자의 친구 목록 조회
        List<Friend> friends = friendRepository.findAllByMemberId(loginId);
        List<String> friendIds = friends.stream()
                .map(friend -> friend.getFriendMember().getId())
                .collect(Collectors.toList());

        // 모든 일반멤버 정보 조회 후 로그인한 사용자와 친구인 회원을 필터링
        List<Member> allMembers = memberRepository.findGeneralUser();
        return allMembers.stream()
                .filter(member -> !member.getId().equals(loginId) && !friendIds.contains(member.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 본인, 친구 제외한 운동전문가 출력
     */
    public List<Member> findTrainerMembers(String loginId) {
        // 로그인한 사용자의 친구 목록 조회
        List<Friend> friends = friendRepository.findAllByMemberId(loginId);
        List<String> friendIds = friends.stream()
                .map(friend -> friend.getFriendMember().getId())
                .collect(Collectors.toList());

        // 모든 운동전문가 정보 조회 후 로그인한 사용자와 친구인 회원을 필터링
        List<Member> allMembers = memberRepository.findTrainer();
        return allMembers.stream()
                .filter(member -> !member.getId().equals(loginId) && !friendIds.contains(member.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 신체정보 추가
     */
    public Long addPhysicalData(String loginId, String date, String height, String weight) {
        LocalDate newDate = LocalDate.parse(date);
        int newHeight = Integer.parseInt(height);
        int newWeigth = Integer.parseInt(weight);
        Member newMember = memberRepository.findOne(loginId);
        UserData userData = new UserData(newMember, newDate, newHeight, newWeigth);
        userDataRepository.save(userData);

        return userData.getId();
    }

    /**
     * 운동데이터 추가
     */
    public Long addExerciseData(String loginId, String date, String exerciseType, String durationMinutes) {
        LocalDate newDate = LocalDate.parse(date);
        int newDurationMinutes = Integer.parseInt(durationMinutes);
        Member newMember = memberRepository.findOne(loginId);
        ExerciseData exerciseData = new ExerciseData(newMember, newDate, exerciseType, newDurationMinutes);
        exerciseDataRepository.save(exerciseData);

        return exerciseData.getId();
    }

    /**
     * 사용자 정보 수정
     */
    public Member update(Member member) {
        Member existingMember = memberRepository.findOne(member.getId());
        if (existingMember != null) {
            // 필요한 필드 업데이트
            existingMember.setPw(member.getPw());
            existingMember.setName(member.getName());
            existingMember.setEmail(member.getEmail());
            existingMember.setAddress(member.getAddress());
            existingMember.setGender(member.getGender());
            existingMember.setExerciseType(member.getExerciseType());
            existingMember.setTrainer(member.isTrainer());

            memberRepository.save(existingMember);
            // 저장 후 반환
            return existingMember;
        } else {
            // 회원이 존재하지 않는 경우 예외 처리
            throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
        }
    }

}
