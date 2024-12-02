방화벽 필터링(특정 ip차단) -> 트래픽 필터링(동일한 ip에서 일정 수 이상 요청 들어오면 차단) -> ips필터링(악성패턴 감지 후 차단, response.setStatus(HttpServletResponse.SC_FORBIDDEN); 사용)
