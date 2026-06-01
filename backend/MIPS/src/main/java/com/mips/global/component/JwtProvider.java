package com.mips.global.component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;


@Slf4j
@Component
public class JwtProvider {
    private final SecretKey secretKey;
    private final long accessExpirationTime;
    private final long refreshExpirationTime;

    public JwtProvider(@Value("${jwt.secret-key}") String secretKey,
                       @Value("${jwt.access-expiration-time}") long accessExpirationTime,
                       @Value("${jwt.refresh-expiration-time}") long refreshExpirationTime)
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes); // HS256 알고리즘
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    // 1. 🟢 Access Token 생성 (수명: 15분) - 프론트엔드가 API 찌를 때 씀
    public String createAccessToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role) // 화면을 위해 권한 정보 탑재
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(secretKey) // HS256 알고리즘과 비밀키로 서명
                .compact();
    }

    // 2. 🔵 Refresh Token 생성 (수명: 14일) - 출입증 만료 시 갱신용으로만 씀
    public String createRefreshToken(String userId) {
        // Refresh 토큰은 해커에게 탈취당할 최악의 경우를 대비해 권한(role)을 담지 않고 가볍게 만듭니다.
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(secretKey)
                .compact();
    }

    public LocalDateTime getExpirationFromToken(String token) {
        // 🌟 최신 버전 공식 빌드 마이그레이션 적용
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)           // 1. setSigningKey -> verifyWith 로 변경 (key 객체 그대로 대입)
                .build()
                .parseSignedClaims(token)  // 2. parseClaimsJws -> parseSignedClaims 로 변경
                .getPayload();             // 3. getBody -> getPayload 로 변경

        // 4. 만료 시간(exp) 추출
        Date expirationDate = claims.getExpiration();

        // 5. Date -> LocalDateTime 변환
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // 진짜 유효한 토큰인지 확인
    /**
     * 1. 토큰 유효성 검증
     * 서명 위변조, 만료 여부, 구조적 결함을 모두 체크합니다.
     */
    public boolean validateToken(String token) {
        try {
            // verifyWith(secretKey)를 통해 우리가 가진 비밀키로 서명을 검증합니다.
            Jwts.parser().verifyWith(secretKey)
                         .build()
                         .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            log.error("유효하지 않은 구성의 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 형식이나 구성의 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 클레임이 비어있습니다.");
        }
        return false; // 하나라도 실패하면 가짜/만료된 토큰이므로 false 반환
    }

    /**
     * 2. 토큰에서 유저 이메일(Subject) 꺼내기
     * 반드시 validateToken()이 true일 때만 호출해야 안전합니다.
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject(); // 토큰 생성 시 subject에 넣었던 email 반환
    }

    /**
     * 3. 토큰에서 유저 권한(Role) 꺼내기
     */
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    public String hmacSha256(String token) throws InvalidKeyException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] hmac = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmac);
    }
}
