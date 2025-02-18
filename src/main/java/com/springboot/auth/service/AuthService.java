//package com.springboot.auth.service;
//
//import com.springboot.auth.jwt.JwtTokenizer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import java.util.Date;
//import java.util.Map;
//
//@Service
//public class AuthService {
//    private final JwtTokenizer jwtTokenizer;
//    private final String secretKey;
//
//    public AuthService(JwtTokenizer jwtTokenizer, @Value("${jwt.key}") String secretKey) {
//        this.jwtTokenizer = jwtTokenizer;
//        this.secretKey = secretKey;
//    }
//
//    public String refreshAccessToken(String refreshToken) {
//        String encodedSecretKey = jwtTokenizer.encodeBase64SecretKey(secretKey);
//
//        // 🔹 RefreshToken 유효성 검사
//        jwtTokenizer.verifySignature(refreshToken, encodedSecretKey);
//
//        // 🔹 refreshToken에서 사용자 정보 추출
//        String subject = jwtTokenizer.getClaims(refreshToken, encodedSecretKey).getBody().getSubject();
//
//        // 🔹 새로운 accessToken 생성
//        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
//        return jwtTokenizer.generateAccessToken(Map.of(), subject, expiration, encodedSecretKey);
//    }
//}
//
