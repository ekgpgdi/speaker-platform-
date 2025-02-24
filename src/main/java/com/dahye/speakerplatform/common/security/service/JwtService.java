package com.dahye.speakerplatform.common.security.service;

import com.dahye.speakerplatform.users.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    public Claims parseToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String generateJwt(Long id, Role role) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(String.valueOf(id)) // 토큰의 주제 (사용자 식별 정보)
                .setIssuedAt(new Date()) // 생성 시각
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 유효 시간
                .claim("role", role) // role 추가
                .signWith(key) // 서명
                .compact();
    }
}
