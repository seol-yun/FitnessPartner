package fitness.securityserver.config;

import fitness.securityserver.service.FirewallFilter;
import fitness.securityserver.service.IPSFilter;
//import fitness.securityserver.service.SQLInjectionFilter;
import fitness.securityserver.service.TrafficFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final IPSFilter ipsFilter;
    private final FirewallFilter firewallFilter;
    private final TrafficFilter trafficFilter;
//    private final SQLInjectionFilter sqlInjectionFilter;

    public SecurityConfig(IPSFilter ipsFilter, FirewallFilter firewallFilter, TrafficFilter trafficFilter) {
        this.ipsFilter = ipsFilter;
        this.firewallFilter = firewallFilter;
        this.trafficFilter = trafficFilter;
//        this.sqlInjectionFilter = sqlInjectionFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);

        // 필터 체인에 필터들 추가
//        http.addFilterBefore(sqlInjectionFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(firewallFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(trafficFilter, FirewallFilter.class);
        http.addFilterBefore(ipsFilter, TrafficFilter.class);

        http
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("**").permitAll()
                                .requestMatchers("api/**").permitAll()
                                .requestMatchers("chat/**").permitAll()
                                .requestMatchers("/chat-websocket/**").permitAll()
                                .requestMatchers("reviews/**").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
