//package fitnessapp.fitnesspartner.service;
//
//import fitnessapp.fitnesspartner.controller.SfcPacket;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//public class FirewallService {
//    private final List<String> allowedIPs = List.of("192.168.1.1", "127.0.0.1"); // 예시 IP 목록
//
//    public SfcPacket filter(SfcPacket packet) {
//        String clientIP = packet.getData().get("clientIP"); // 클라이언트 IP를 패킷에서 가져옵니다.
//
//        if (allowedIPs.contains(clientIP)) {
//            packet.setAuthenticated(true); // 허용된 IP라면 인증된 것으로 표시합니다.
//        } else {
//            packet.setAuthenticated(false); // 허용되지 않은 IP라면 인증되지 않은 것으로 표시합니다.
//        }
//        return packet;
//    }
//}
