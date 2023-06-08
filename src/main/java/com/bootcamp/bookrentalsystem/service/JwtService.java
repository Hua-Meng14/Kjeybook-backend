package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.BadRequestException;
import com.bootcamp.bookrentalsystem.exception.ForbiddenException;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.swing.text.html.Option;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private String jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private String refreshExpiration;

    private UserRepository userRepository;

    @Autowired
    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(User userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            User userDetails
    ) {
        return buildToken(extraClaims, userDetails, Long.parseLong(jwtExpiration));
    }

    public String generateRefreshToken(com.bootcamp.bookrentalsystem.model.User userDetails) {
        return buildToken(new HashMap<>(), userDetails, Long.parseLong(refreshExpiration));
    }

    public String buildToken(
            Map<String, Object> extraClaims,
            User userDetails,
            long expiration
    ) {
        Claims claims = Jwts.claims();
        claims.put("role", userDetails.getRole());
        claims.put("email", userDetails.getEmail());

        if (extraClaims != null) {
            claims.putAll(extraClaims);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isAdminToken(String token) {
        String jwtTokenWithoutInvalidChars = token.replaceAll("[^A-Za-z0-9+/=]", "");
        byte[] tokenBytes = Base64.getUrlDecoder().decode(jwtTokenWithoutInvalidChars);
        String decodedToken = new String(tokenBytes, StandardCharsets.UTF_8);
        byte[] secretBytes = Decoders.BASE64.decode(decodedToken);

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretBytes))
                .build()
                .parseClaimsJws(token);

        Claims claims = claimsJws.getBody();

        String role = claims.get("role", String.class);

        // Perform authorization logic based on the extracted information
        return "ADMIN".equals(role);
    }

    public boolean isTokenExpired(String token) {
        try {
            String jwtTokenWithoutInvalidChars = token.replaceAll("[^A-Za-z0-9+/=]", "");
            byte[] tokenBytes = Base64.getUrlDecoder().decode(jwtTokenWithoutInvalidChars);
            String decodedToken = new String(tokenBytes, StandardCharsets.UTF_8);
            byte[] secretBytes = Decoders.BASE64.decode(decodedToken);

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretBytes))
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            Date expiration = claims.getExpiration();

            // Token expiration logic
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUserToken(String token, Long userId) {
        try {
            String jwtTokenWithoutInvalidChars = token.replaceAll("[^A-Za-z0-9+/=]", "");
            byte[] tokenBytes = Base64.getUrlDecoder().decode(jwtTokenWithoutInvalidChars);
            String decodedToken = new String(tokenBytes, StandardCharsets.UTF_8);
            byte[] secretBytes = Decoders.BASE64.decode(decodedToken);

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretBytes))
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            // Extract the necessary information from the token, such as user ID or email
            String tokenEmail = claims.get("email", String.class);

            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                // Perform authorization logic based on email
                return tokenEmail.equals(user.getEmail());
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}