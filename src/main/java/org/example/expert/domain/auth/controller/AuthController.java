package org.example.expert.domain.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest, HttpServletResponse response) {

        String bearerToken = authService.signup(signupRequest);

        //문자열로 반환하던 Bearer Token을 쿠키에 저장
        Cookie cookie = CookieUtil.createTokenCookie(bearerToken, 180);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<Void> signin(@Valid @RequestBody SigninRequest signinRequest, HttpServletResponse response) {

        String bearerToken = authService.signin(signinRequest);

        //문자열로 반환하던 Bearer Token을 쿠키에 저장
        Cookie cookie = CookieUtil.createTokenCookie(bearerToken, 180);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
