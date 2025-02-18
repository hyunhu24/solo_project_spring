package com.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
WebConfig
로컬호스트 3000번 포트에서 실행되는 프론트엔드 애플리케이션과
백엔드 API 서버 간의 CORS 연결을 설정하는 것
*/
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    // CORS 설정을 정의
    /*
        CORS(Cross-Origin Resource Sharing)
        출처가 다른 자원들을 공유한다는 뜻
        한 출처에 있는 자원에서 다른 출처에 있는 자원에 접근하도록 하는 개념
        출처란?
        Protocol + Host + Port 3가지가 같으면 동일 출처(Origin)라고 함
        즉 다른 출처 요청일 경우 CORS 정책에 준수하여 요청해야만 정상적으로 응답을 받음
    */
    public void addCorsMappings(CorsRegistry registry) {
        // "/api/"로 시작하는 모든 엔드포인트에 CORS 설정을 적용
        registry.addMapping("/**")
                // http://localhost:3000 출처(origin) 에서의 요청을 허용
                .allowedOrigins("http://localhost:3000")  // 프론트엔드 출처 허용
                // 허용된 HTTP 메서드 지정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // OPTIONS 메서드 추가
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 쿠키 허용 (필요한 경우)
    }
}

