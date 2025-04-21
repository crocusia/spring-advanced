package org.example.expert.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
    @Spy
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtUtil.init();
    }

    @DisplayName("jwtToken이 제대로 생성된다")
    @Test
    public void testCreateValidJwtToken() throws IOException, ServletException {
        String jwt = jwtUtil.createToken(1L, "email.com", UserRole.USER);

        // jwt가 null이거나 빈 문자열이 아닌지 확인
        Assertions.assertNotNull(jwt, "JWT는 null일 수 없습니다.");
        Assertions.assertFalse(jwt.isEmpty(), "JWT는 빈 문자열일 수 없습니다.");
    }

    @DisplayName("유효한 토큰의 경우 로그인 필터를 통과한다")
    @Test
    public void testValidJwtToken() throws IOException, ServletException {
        // given
        //토큰 생성
        String jwt = jwtUtil.createToken(1L, "email.com", UserRole.USER);

        Cookie jwtCookie = new Cookie("token", jwt);
        when(httpRequest.getCookies()).thenReturn(new Cookie[] {jwtCookie});
        when(httpRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
        when(httpRequest.getRequestURI()).thenReturn("/some/uri");

        // when
        jwtFilter.doFilter(httpRequest, httpResponse, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(httpRequest, httpResponse);
    }

    @DisplayName("만료된 토큰인 경우 SC_UNAUTHORIZED 예외가 전달된다")
    @Test
    public void testExpiredJwtException() throws IOException, ServletException {
        String secretKey = "U3BhcnRh7IiZ66Co7KO87LCo6rCc7J246rO87KCc7Iuc7YGs66a/7YKk66eM65Ok6riw";
        byte[] keyBytes = Base64.getDecoder().decode(Base64.getEncoder().encodeToString(secretKey.getBytes()));
        Key key = Keys.hmacShaKeyFor(keyBytes);
        // given
        //만료된 토큰 생성
        String jwt = Jwts.builder()
                .setSubject(String.valueOf(1L))
                .claim("email", "email.com")
                .claim("userRole", UserRole.USER.name())
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000)) // 10초 전 발급
                .setExpiration(new Date(System.currentTimeMillis() - 5000)) // 5초 전에 만료
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Cookie jwtCookie = new Cookie("token", jwt);
        when(httpRequest.getCookies()).thenReturn(new Cookie[] {jwtCookie});
        when(httpRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
        when(httpRequest.getRequestURI()).thenReturn("/some/uri");

        // when
        jwtFilter.doFilter(httpRequest, httpResponse, filterChain);

        // then
        verify(httpResponse, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
    }
}