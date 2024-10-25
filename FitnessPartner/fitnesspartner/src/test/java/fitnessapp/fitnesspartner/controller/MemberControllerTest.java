//package fitnessapp.fitnesspartner.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.Cookie;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class MemberControllerTest {
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void testLogout() throws Exception {
//        // 로그인을 시행하여 세션을 생성합니다.
//        String loginId = "testUser";
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/members/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(Map.of("id", loginId, "pw", "password"))))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        // 세션 생성 후 로그아웃을 시행합니다.
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/members/logout"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        // 로그아웃 후 세션을 확인합니다.
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/members/info")
//                        .cookie(new Cookie("loginId", loginId)))
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
//    }
//}