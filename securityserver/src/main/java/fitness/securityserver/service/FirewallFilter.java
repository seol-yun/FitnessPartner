package fitness.securityserver.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 특정 IP 차단
 */
@Component
public class FirewallFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(FirewallFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String remoteAddress = request.getRemoteAddr();

        // 예: 특정 IP를 차단
        if (remoteAddress.equals("222.118.27.39")) {
            // 차단된 IP에 대한 로그 출력
            logger.warn("차단된 아이피: {}", remoteAddress);

            // 응답 설정
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied(차단된 IP)");
            return;
        }

        // 정상적인 요청은 다음 필터로
        filterChain.doFilter(request, response);
    }
}
