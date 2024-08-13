package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.config.EncryptionUtil;
import fitnessapp.fitnesspartner.controller.SfcPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthenticationService {
    @Autowired
    private MemberService memberService;
    @Autowired
    private EncryptionUtil encryptionUtil;

    public SfcPacket authenticate(SfcPacket packet) {
        Map<String, String> data = packet.getData();
        String id = data.get("id");
        String encryptedPw = data.get("pw");

        // 데이터베이스에서 사용자 정보 조회
        String storedEncryptedPassword = memberService.findOne(id).getPw();
        if (storedEncryptedPassword != null && encryptionUtil.matches(encryptedPw, storedEncryptedPassword)) {
            packet.setAuthenticated(true);
        } else {
            packet.setAuthenticated(false);
        }
        return packet;
    }
}

