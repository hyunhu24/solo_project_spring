//package com.springboot.auth.controller;
//
//import com.springboot.auth.jwt.JwtTokenizer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Date;
//import java.util.Map;
//
//// AuthController.java
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//    private final JwtTokenizer jwtTokenizer;
//
//    @Value("${jwt.key}")
//    private String secretKey;  // 환경 변수에서 Secret Key 가져오기
//
//    public AuthController(JwtTokenizer jwtTokenizer) {
//        this.jwtTokenizer = jwtTokenizer;
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
//        if (refreshToken == null) {
//            System.out.println("No refresh token provided in cookies!");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token provided");
//        }
//
//        try {
//            //  RefreshToken 유효성 검사
//            jwtTokenizer.verifySignature(refreshToken, jwtTokenizer.encodeBase64SecretKey(secretKey));
//
//            //  refreshToken에서 사용자 정보 추출
//            String subject = jwtTokenizer.getClaims(refreshToken, jwtTokenizer.encodeBase64SecretKey(secretKey))
//                    .getBody().getSubject();
//
//            //  새로운 accessToken 생성
//            Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
//            String accessToken = jwtTokenizer.generateAccessToken(
//                    Map.of(), subject, expiration, jwtTokenizer.encodeBase64SecretKey(secretKey)
//            );
//
//            return ResponseEntity.ok(Map.of("accessToken", accessToken));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
//        }
//    }
//
//}
//
