package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.domain.UserData;
import fitnessapp.fitnesspartner.dto.UserDataDTO;
import fitnessapp.fitnesspartner.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDataService {

    private final UserDataRepository userDataRepository;

    public List<UserDataDTO> getAllPhysicalDataByMemberId(String memberId) {
        List<UserData> userDataList = userDataRepository.findAllPhysicalDataByMemberId(memberId);
        return userDataList.stream()
                .map(userData -> new UserDataDTO(userData.getDate(), userData.getHeight(), userData.getWeight()))
                .collect(Collectors.toList());
    }
}
