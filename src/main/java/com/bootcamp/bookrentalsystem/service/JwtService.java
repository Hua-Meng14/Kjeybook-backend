package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.BadRequestException;
import com.bootcamp.bookrentalsystem.exception.ForbiddenException;
import com.bootcamp.bookrentalsystem.exception.UnauthorizedException;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
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
        if (token == null || token.isEmpty()) {
            throw new UnauthorizedException("Token required");
        }

        if (!token.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid token format");
        }

        String jwtToken = token.substring(7);

//        System.out.println("--------------BEARER AUTH TOKEN: " + jwtToken);

        Claims claims = decodeToken(jwtToken);

//        System.out.println("----------------TOKEN CLAIMS: " + claims);

        String role = claims.get("role", String.class);
        // Perform authorization logic based on the extracted information
        return "ADMIN".equals(role);
    }

    public boolean isTokenExpired(String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new UnauthorizedException("Token required");
            }

            if (!token.startsWith("Bearer ")) {
                throw new UnauthorizedException("Invalid token format");
            }

            String jwtToken = token.substring(7);

            Claims claims = decodeToken(jwtToken);

            Date expiration = claims.getExpiration();

            // Token expiration logic
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUserToken(String token, Long userId) {
        try {
            if (token == null || token.isEmpty()) {
                throw new UnauthorizedException("Token required");
            }

            if (!token.startsWith("Bearer ")) {
                throw new UnauthorizedException("Invalid token format");
            }

            String jwtToken = token.substring(7);

            Claims claims = decodeToken(jwtToken);

//            System.out.println("--------------------- TOKEN CLAIMS: "+ claims);

            String tokenEmail = claims.get("email", String.class);

            Optional<User> existingUser = userRepository.findById(userId);
//            System.out.println("---------------USER EMAIL: "+ existingUser.get().getEmail());
//            System.out.println("---------------TOKEN EMAIL: "+ tokenEmail);
//            System.out.println("---------------TOKEN IS MATCHED: "+ existingUser.get().getEmail().equals(tokenEmail));
            return existingUser.map(user -> tokenEmail.equals(user.getEmail())).orElse(false);
        } catch (Exception e) {
            return false;
        }
    }


    public Claims decodeToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new ForbiddenException("Invalid Token!!");
        }
    }
}