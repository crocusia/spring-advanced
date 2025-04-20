package org.example.expert.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
    public static Cookie createTokenCookie(String tokenValue, int maxAgeInSeconds) {
        Cookie cookie = new Cookie("token", tokenValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds); // 예: 3 * 60
        return cookie;
    }
}
