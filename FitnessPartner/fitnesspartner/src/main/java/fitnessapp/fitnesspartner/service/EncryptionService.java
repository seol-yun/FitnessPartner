package fitnessapp.fitnesspartner.service;

import fitnessapp.fitnesspartner.config.EncryptionUtil;
import fitnessapp.fitnesspartner.controller.SfcPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EncryptionService {
    @Autowired
    private EncryptionUtil encryptionUtil;

    public SfcPacket encrypt(SfcPacket packet) {
        System.out.println("!$!@$!@$!@$!@$21");
        Map<String, String> data = packet.getData();
        String encryptedPw = encryptionUtil.encrypt(data.get("pw"));
        data.put("pw", encryptedPw);
        packet.setData(data);
        return packet;
    }
}