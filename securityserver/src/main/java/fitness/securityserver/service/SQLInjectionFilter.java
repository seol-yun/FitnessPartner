//package fitness.securityserver.service;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Enumeration;
//import java.util.regex.Pattern;
//
///**
// * SQL Injection 패턴을 감지하고 차단하는 필터
// */
//@Component
//public class SQLInjectionFilter extends OncePerRequestFilter {
//
//    // SQL Injection을 탐지하는 정규식 패턴
//    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
//            "(?i)(union\\s+select|select\\s+.*\\s+from|or\\s+1=1|and\\s+1=1|--|;|insert\\s+into|drop\\s+table|delete\\s+from|update\\s+.*\\s+set)",
//            Pattern.CASE_INSENSITIVE);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        // 요청 파라미터와 헤더 검사
//        if (hasSqlInjection(request)) {
//            // SQL Injection이 감지된 경우 요청을 차단하고 에러 응답
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.setContentType("text/plain; charset=UTF-8");
//            response.getWriter().write("Request blocked due to SQL injection attempt");
//            return;
//        }
//
//        // SQL Injection이 감지되지 않으면 다음 필터로 요청 전달
//        filterChain.doFilter(request, response);
//    }
//
//    // SQL Injection을 감지하는 메서드
//    private boolean hasSqlInjection(HttpServletRequest request) {
//        // 모든 요청 파라미터 검사
//        for (String param : request.getParameterMap().keySet()) {
//            String value = request.getParameter(param);
//            if (value != null && SQL_INJECTION_PATTERN.matcher(value).find()) {
//                return true;
//            }
//        }
//
//        // 요청 헤더 검사
//        Enumeration<String> headerNames = request.getHeaderNames();
//        if (headerNames != null) {
//            while (headerNames.hasMoreElements()) {
//                String headerName = headerNames.nextElement();
//                String headerValue = request.getHeader(headerName);
//                if (headerValue != null && SQL_INJECTION_PATTERN.matcher(headerValue).find()) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//}
