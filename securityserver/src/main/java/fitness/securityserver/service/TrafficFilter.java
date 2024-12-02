package fitness.securityserver.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 요청 제한: 1분 내에 100회 이상의 요청이 들어오면 해당 IP에서 더 이상 요청을 보낼 수 없도록 차단.
 * 시간 기반 관리: 1분이 지나면 카운트를 초기화하여 새로운 요청이 가능하게 설정.
 * 429 ("Too Many Requests")는 클라이언트가 일정 시간 내에 너무 많은 요청을 서버에 보낸 경우 사용되는 상태 코드
 */
@Component
public class TrafficFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TrafficFilter.class);

    private final Map<String, RequestInfo> requestCounts = new HashMap<>();

    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final int ONE_MINUTE_IN_MILLIS = 60 * 1000;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIP = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        // 해당 IP의 요청 정보를 가져오거나 새로 생성
        RequestInfo requestInfo = requestCounts.getOrDefault(clientIP, new RequestInfo(0, currentTime));

        // 시간 차이 계산
        long timeElapsed = currentTime - requestInfo.getLastRequestTime();

        if (timeElapsed > ONE_MINUTE_IN_MILLIS) {
            // 1분 이상 경과했으면 카운트 리셋
            requestInfo.setCount(1);
            requestInfo.setLastRequestTime(currentTime);
        } else {
            // 1분 이내의 요청이면 카운트 증가
            requestInfo.setCount(requestInfo.getCount() + 1);
        }

        // 요청이 제한을 초과한 경우 차단
        if (requestInfo.getCount() > MAX_REQUESTS_PER_MINUTE) {
            logger.warn("차단된 IP: {} - 너무 많은 요청", clientIP);  // 차단된 IP와 사유 로그 출력
            response.setStatus(429);  // 직접 429 상태 코드 사용
            response.getWriter().write("Too many requests (차단된 IP)");
            return;
        }

        // 최신 요청 정보를 저장
        requestCounts.put(clientIP, requestInfo);

        // 정상적인 요청은 계속 진행
        filterChain.doFilter(request, response);
    }

    // 요청 정보를 저장할 클래스
    @Getter
    @Setter
    private static class RequestInfo {
        private int count;  // 요청 횟수
        private long lastRequestTime;  // 마지막 요청 시간

        public RequestInfo(int count, long lastRequestTime) {
            this.count = count;
            this.lastRequestTime = lastRequestTime;
        }
    }
}
