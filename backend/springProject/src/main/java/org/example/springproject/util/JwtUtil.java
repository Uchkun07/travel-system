package org.example.springproject.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.springproject.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成、解析和验证JWT令牌
 */
@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 生成密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param username 用户名
     * @param rememberMe 是否记住我
     * @return JWT令牌
     */
    public String generateToken(Long userId, String username, boolean rememberMe) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        
        long expirationTime = rememberMe ? jwtProperties.getExpiration() : jwtProperties.getExpirationShort();
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成包含完整用户信息的JWT令牌
     * @param userId 用户ID
     * @param username 用户名
     * @param email 邮箱
     * @param fullName 全名
     * @param avatar 头像
     * @param phone 手机号
     * @param gender 性别
     * @param birthday 生日
     * @param rememberMe 是否记住我
     * @return JWT令牌
     */
    public String generateTokenWithUserInfo(Long userId, String username, String email, 
                                           String fullName, String avatar, String phone, 
                                           Byte gender, Date birthday, boolean rememberMe) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("email", email);
        claims.put("fullName", fullName);
        claims.put("avatar", avatar);
        claims.put("phone", phone);
        claims.put("gender", gender);
        // 将生日转换为时间戳存储
        if (birthday != null) {
            claims.put("birthday", birthday.getTime());
        }
        
        long expirationTime = rememberMe ? jwtProperties.getExpiration() : jwtProperties.getExpirationShort();
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从令牌中获取所有声明
     * @param token JWT令牌
     * @return 声明对象
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object userId = claims.get("userId");
        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }
        return null;
    }

    /**
     * 从令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从令牌中获取邮箱
     * @param token JWT令牌
     * @return 邮箱
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    /**
     * 从令牌中获取全名
     * @param token JWT令牌
     * @return 全名
     */
    public String getFullNameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("fullName", String.class);
    }

    /**
     * 从令牌中获取头像
     * @param token JWT令牌
     * @return 头像URL
     */
    public String getAvatarFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("avatar", String.class);
    }

    /**
     * 从令牌中获取手机号
     * @param token JWT令牌
     * @return 手机号
     */
    public String getPhoneFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("phone", String.class);
    }

    /**
     * 从令牌中获取性别
     * @param token JWT令牌
     * @return 性别
     */
    public Byte getGenderFromToken(String token) {
        Claims claims = parseToken(token);
        Object gender = claims.get("gender");
        if (gender instanceof Number) {
            return ((Number) gender).byteValue();
        }
        return null;
    }

    /**
     * 从令牌中获取生日
     * @param token JWT令牌
     * @return 生日
     */
    public Date getBirthdayFromToken(String token) {
        Claims claims = parseToken(token);
        Object birthday = claims.get("birthday");
        if (birthday instanceof Number) {
            return new Date(((Number) birthday).longValue());
        }
        return null;
    }

    /**
     * 检查令牌是否过期
     * @param token JWT令牌
     * @return true表示已过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证令牌
     * @param token JWT令牌
     * @return true表示有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取令牌过期时间戳
     * @param token JWT令牌
     * @return 过期时间戳(毫秒)
     */
    public Long getExpirationTime(String token) {
        try {
            return parseToken(token).getExpiration().getTime();
        } catch (Exception e) {
            return null;
        }
    }
}
