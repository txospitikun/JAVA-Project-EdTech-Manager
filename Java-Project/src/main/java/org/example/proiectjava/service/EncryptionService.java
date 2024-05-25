package org.example.proiectjava.service;

import com.google.common.hash.Hashing;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class EncryptionService {

    private static Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    private static final String SECRET_KEY = "9BDEoeaQcRBwZHJz6tZXcVR4IvR1uGeln8kottoXa+U3DiItJtB4PG7pe3VXFKP8jv/2p1ZsTKyOTRqIcm+SkQ==";
    private static final long EXPIRATION_TIME = 86400000;

    public static String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static boolean authenticateToken(String token) {
        try {
            logger.info("Starting authentification of JWT Token.");

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);

//            return claims.getBody().getExpiration().before(new Date());
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public static String encryptSHA256(String stringToEncode)
    {
        return Hashing.sha256()
                .hashString(stringToEncode, StandardCharsets.UTF_8)
                .toString();
    }
}