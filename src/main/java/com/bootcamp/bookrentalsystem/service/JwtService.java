package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.BadRequestException;
import com.bootcamp.bookrentalsystem.exception.ForbiddenException;
import com.bootcamp.bookrentalsystem.exception.IllegalArgumentException;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
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

    private boolean isTokenNotExpired(Date expiration) {
//        System.out.println("-----------EXPIRATION: " + expiration);
        return expiration != null && expiration.after(new Date());

    }

    public boolean isValidAdminToken(String token) {

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token required");
        }

        if (!token.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token format");
        }

        String jwtToken = token.substring(7);

        Claims claims = decodeToken(jwtToken);

        // Get token role
        String role = claims.get("role", String.class);
        // Get token expiration
        Date expiration = claims.getExpiration();

        // Check is token role is ADMIN type
        boolean isAdminRole = "ADMIN".equals(role);
        // Check token is NOT expired
        boolean isNotExpired = isTokenNotExpired(expiration);

        return isAdminRole && isNotExpired;
    }

    public boolean isValidUserToken(String token, Long userId) {

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token required");
        }

        if (!token.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token format");
        }

        String jwtToken = token.substring(7);

        Claims claims = decodeToken(jwtToken);

        // Get token role
        String tokenEmail = claims.get("email", String.class);
        // Get token expiration
        Date expiration = claims.getExpiration();

        // Find existingUser with userId
        Optional<User> existingUser = userRepository.findById(userId);
        // Check if tokenEmail equals to existingUser's email
        boolean emailMatched = existingUser.map(user -> tokenEmail.equals(user.getEmail())).orElse(false);
        // Check token is NOT expired
        boolean isNotExpired = isTokenNotExpired(expiration);

//        System.out.println("----------------EMAIL MATCHED: " + emailMatched);
//        System.out.println("----------------TOKEN IS NOT EXPIRED: " + isNotExpired);
//        System.out.println("----------------EMAIL MATCHED AND TOKEN NOT EXPIRED: " + (emailMatched && isNotExpired));

        return emailMatched && isNotExpired;
    }


//    public boolean isAdminToken(String token) {0
//        if (token == null || token.isEmpty()) {
//            throw new UnauthorizedException("Token required");
//        }
//
//        if (!token.startsWith("Bearer ")) {
//            throw new UnauthorizedException("Invalid token format");
//        }
//
//        String jwtToken = token.substring(7);
//
//        Claims claims = decodeToken(jwtToken);
//
//        String role = claims.get("role", String.class);
//        // Perform authorization logic based on the extracted information
//        return "ADMIN".equals(role);
//    }
//
//    public boolean isTokenExpired(String token) {
//        try {
//            if (token == null || token.isEmpty()) {
//                throw new UnauthorizedException("Token required");
//            }
//
//            if (!token.startsWith("Bearer ")) {
//                throw new UnauthorizedException("Invalid token format");
//            }
//
//            String jwtToken = token.substring(7);
//
//            Claims claims = decodeToken(jwtToken);
//
//            Date expiration = claims.getExpiration();
//
//            // Token expiration logic
//            return expiration.before(new Date());
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public boolean isValidToken(String token) {
//        try {
//            if (token == null || token.isEmpty()) {
//                throw new UnauthorizedException("Token required");
//            }
//
//            System.out.println("-------------------TOKEN: " + token);
//
//            Claims claims = decodeToken(token);
//            System.out.println("-------------------TOKEN CLAIMS: " + claims);
//
//            Date expiration = claims.getExpiration();
//
//            String tokenEmail = claims.get("email", String.class);
//            Optional<User> existingUser = userRepository.findByEmail(tokenEmail);
//
//            System.out.println("------- IS IT BELONG TO A USER: " + existingUser.isPresent());
//            System.out.println("------- IS IT EXPIRED: " + expiration.before(new Date()));
//
//            return existingUser.isPresent() && expiration.before(new Date());
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public boolean isUserToken(String token, Long userId) {
//        try {
//            if (token == null || token.isEmpty()) {
//                throw new UnauthorizedException("Token required");
//            }
//
//            if (!token.startsWith("Bearer ")) {
//                throw new UnauthorizedException("Invalid token format");
//            }
//
//            String jwtToken = token.substring(7);
//
//            Claims claims = decodeToken(jwtToken);
//
//            String tokenEmail = claims.get("email", String.class);
//
//            Optional<User> existingUser = userRepository.findById(userId);
////            System.out.println("---------------USER EMAIL: "+ existingUser.get().getEmail());
////            System.out.println("---------------TOKEN EMAIL: "+ tokenEmail);
////            System.out.println("---------------TOKEN IS MATCHED: "+ existingUser.get().getEmail().equals(tokenEmail));
//            return existingUser.map(user -> tokenEmail.equals(user.getEmail())).orElse(false);
//        } catch (Exception e) {
//            return false;
//        }
//    }


}