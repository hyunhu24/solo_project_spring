package com.springboot.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.auth.dto.LoginDto;
import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.user.entity.User;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenizer jwtTokenizer) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenizer = jwtTokenizer;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws ServletException, IOException {
        User user = (User) authResult.getPrincipal();

        String accessToken = delegateAccessToken(user);
        String refreshToken = delegateRefreshToken(user);

//        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
//        refreshCookie.setHttpOnly(true);  // JavaScript에서 접근 불가능 (XSS 공격 방지)
//        refreshCookie.setSecure(false);   // 개발 환경에서는 false (운영 환경에서는 true)
//        refreshCookie.setPath("/");       // 모든 경로에서 사용 가능하게 설정
//        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 동안 유지

//        response.addCookie(refreshCookie);  // 기존 코드 유지

// 명시적으로 Set-Cookie 헤더 추가 (디버깅용)

        String cookieValue = String.format("refreshToken=%s; Path=/; Max-Age=%d; HttpOnly; Secure=%s; SameSite=None",
                refreshToken, 7 * 24 * 60 * 60, "false");
        response.setHeader("Set-Cookie", cookieValue);
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
//        response.setHeader("Refresh", refreshToken);

//        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
//        refreshCookie.setHttpOnly(true);
//        refreshCookie.setSecure(false); // 실제 배포 시 true
//        refreshCookie.setPath("/");
//        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
//        refreshCookie.setAttribute("SameSite", "None"); // CORS 요청 시 필요
//
//        response.addCookie(refreshCookie);
        System.out.println("🔹 response에 저장한 refreshToken 쿠키: " + refreshToken);

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }



    private String delegateAccessToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getEmail());
        claims.put("roles", user.getRoles());

        String subject = user.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration2(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String accessToken = jwtTokenizer.generateAccessToken(claims,subject,expiration,base64EncodedSecretKey);

        return accessToken;
    }

//    private String delegateRefreshToken(User user){
//        String subject = user.getEmail();
//        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes()); // ✅ 7일 설정
//        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
//
//        return jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);
//    }

    private String delegateRefreshToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getEmail());
        claims.put("roles", user.getRoles());

        String subject = user.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String refreshToken = jwtTokenizer.generateRefreshToken(claims, subject,expiration,base64EncodedSecretKey);

        return refreshToken;
    }


}