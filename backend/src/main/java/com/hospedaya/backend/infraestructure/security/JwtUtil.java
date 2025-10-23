package com.hospedaya.backend.infraestructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key signingKey;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${app.jwt.secret:Zm9yX2Rldl9vbmx5X2RvX25vdF91c2VfdGhpc19zZWNyZXRfcGxlYXNlX2NoYW5nZV9tZV9pbl9wcm9k}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMillis) {
        // Build an HMAC key of at least 256 bits from the provided secret, accepting Base64 or plain text.
        byte[] keyBytes = null;
        try {
            byte[] decoded = Decoders.BASE64.decode(secret);
            if (decoded != null && decoded.length >= 32) {
                keyBytes = decoded;
            } else {
                // Too short after decode; fall back to SHA-256 derivation of the original secret string
                keyBytes = sha256(secret.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IllegalArgumentException ex) {
            // Not valid Base64; derive with SHA-256 from the plain text secret
            keyBytes = sha256(secret.getBytes(StandardCharsets.UTF_8));
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    private static byte[] sha256(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input);
        } catch (Exception e) {
            // This should never happen; as a safe fallback, pad/truncate input to 32 bytes
            byte[] out = new byte[32];
            int len = Math.min(input.length, 32);
            System.arraycopy(input, 0, out, 0, len);
            return out;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String username, Map<String, Object> extraClaims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
