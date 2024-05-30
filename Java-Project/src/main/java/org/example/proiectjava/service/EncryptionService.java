package org.example.proiectjava.service;

import com.google.common.hash.Hashing;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class EncryptionService {

    private static final String SECRET_KEY = "9BDEoeaQcRBwZHJz6tZXcVR4IvR1uGeln8kottoXa+U3DiItJtB4PG7pe3VXFKP8jv/2p1ZsTKyOTRqIcm+SkQ==";
    private static final long EXPIRATION_TIME = 864_000_000;

    public static String generateToken(String UserInfo) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        JSONObject parsedUserInfo = new JSONObject(UserInfo);

        return Jwts.builder()
                .setSubject(parsedUserInfo.getString("Username"))
                .claim("privilege", parsedUserInfo.getString("Privilege"))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static int authenticateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);

            String privilege = claims.getBody().get("privilege", String.class);
            return Integer.parseInt(privilege);

        } catch (Exception e) {
            return 0;
        }
    }

    public static String encryptSHA256(String stringToEncode)
    {
        return Hashing.sha256()
                .hashString(stringToEncode, StandardCharsets.UTF_8)
                .toString();
    }
}