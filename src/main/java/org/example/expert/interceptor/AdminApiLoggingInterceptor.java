package org.example.expert.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.expert.domain.user.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class AdminApiLoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AdminApiLoggingInterceptor.class);

    //어드민 관리자가 접근할 수 있는 API가 컨트롤러에 오기 전에 검사하는 preHandle
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){

        String uri = request.getRequestURI();
        String method = request.getMethod();

        //요청 정보를 사전 처리 -> 어드민 사용자인지 확인
        UserRole userRole = UserRole.of((String) request.getAttribute("userRole"));

        if (!UserRole.ADMIN.equals(userRole)) {
            logger.warn("어드민 권한 없음: {}", uri);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false; // 요청 중단
        }

        //인증 성공 시, 요청 시각과 URL을 로깅
        logger.info("요청 시간: {}, URL: {} {}", LocalDateTime.now(), method, uri);
        return true;
    }
}
