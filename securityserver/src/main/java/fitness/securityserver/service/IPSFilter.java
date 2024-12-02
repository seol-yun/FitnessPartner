package fitness.securityserver.service;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * 악성패턴 감지 후 차단
 */
@Component
public class IPSFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // User-Agent 헤더 분석
        String userAgent = request.getHeader("User-Agent");

        // 악성 패턴 감지
        if (userAgent != null && userAgent.contains("MaliciousBot")) {
            // 비정상적인 요청 차단
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Request blocked by IPS");
            return;
        }

        // 정상적인 요청일 경우 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
