package com.springboot.auth.filter;

import com.springboot.auth.AuthorityUtils;
import com.springboot.auth.jwt.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final AuthorityUtils authorityUtils;

    public JwtVerificationFilter(JwtTokenizer jwtTokenizer, AuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        try{
//            Map<String,Object> claims = verifyJws(request);
//            setAuthenticationToContext(claims);
//        } catch (SignatureException se) {
//            request.setAttribute("exception", se);
//        } catch (ExpiredJwtException ee) {
//            // 쿠키에 담긴 리프레시 토큰 검증
//                // JwtTokenizer 에서
//            // 리프레시 토큰이 없으면 예외
//                // request.setAttribute("exception", ee);
//            // 검증이 통과하면 accessToken 만료시 재발행?
//
//        } catch (Exception e) {
//            request.setAttribute("exception", e);
//        }
//
//        filterChain.doFilter(request,response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, Object> claims = verifyJws(request);
            setAuthenticationToContext(claims);
        } catch (SignatureException se) {
            request.setAttribute("exception", se);
        } catch (ExpiredJwtException ee) {
            // 액세스 토큰이 만료되었을 때 리프레시 토큰 검증 및 재발급 로직 추가
            String refreshToken = getRefreshTokenFromCookie(request);
            System.out.println("🔹 요청에서 받은 refreshToken: " + refreshToken);
            if (refreshToken != null) {
                try {
                    // 리프레시 토큰 검증
                    jwtTokenizer.verifySignature(refreshToken, jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey()));

                    // 새로운 액세스 토큰 발급
                    String newAccessToken = regenerateAccessToken(refreshToken);
                    response.setHeader("Authorization", "Bearer " + newAccessToken);

                    // 새로 발급한 액세스 토큰으로 요청을 다시 처리할 수 있도록 SecurityContext에 저장
                    Map<String, Object> claims = jwtTokenizer.getClaims(newAccessToken, jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey())).getBody();
                    setAuthenticationToContext(claims);

                } catch (Exception refreshEx) {
                    request.setAttribute("exception", refreshEx);
                }
            } else {
                request.setAttribute("exception", ee); // 리프레시 토큰이 없으면 예외 설정
            }
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    // 쿠키에서 리프레시 토큰 가져오기
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 리프레시 토큰을 이용해 새로운 액세스 토큰 발급
    private String regenerateAccessToken(String refreshToken) {
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        Claims claims = jwtTokenizer.getClaims(refreshToken, base64EncodedSecretKey).getBody();

        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
        return jwtTokenizer.generateAccessToken(claims, claims.getSubject(), expiration, base64EncodedSecretKey);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");

        return authorization == null || !authorization.startsWith("Bearer ");
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        return request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name());
//    }

    private Map<String, Object> verifyJws(HttpServletRequest request){
        String jws = request.getHeader("Authorization").replace("Bearer ", "");
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();

        return claims;
    }

    private void setAuthenticationToContext(Map<String, Object> claims){
        String username = (String) claims.get("username");
        List<GrantedAuthority> authorities = authorityUtils.createAuthorities((List)claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username,null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
