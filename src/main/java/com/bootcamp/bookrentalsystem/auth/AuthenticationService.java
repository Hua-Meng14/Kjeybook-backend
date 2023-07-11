package com.bootcamp.bookrentalsystem.auth;

import com.bootcamp.bookrentalsystem.exception.BadRequestException;
import com.bootcamp.bookrentalsystem.exception.IllegalArgumentException;
import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.exception.UnauthorizedException;
import com.bootcamp.bookrentalsystem.model.RegisterUser;
import com.bootcamp.bookrentalsystem.model.Token;
import com.bootcamp.bookrentalsystem.model.TokenType;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.TokenRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import com.bootcamp.bookrentalsystem.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse register(RegisterUser registerUser) {
        String username = registerUser.getUsername();
        String email = registerUser.getEmail();
        String password = registerUser.getPassword();
        String phoneNumber = registerUser.getPhoneNumber();
//        String role = registerUser.getRole();

        // Check if the username or email already exists in the database
        if (userRepository.findByUsername(registerUser.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.findByEmail(registerUser.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        // Create a new user entity
        User newUser = new User(username, email, passwordEncoder.encode(password), "USER", phoneNumber);

        // System.out.println("-------------NEW USER-------------: "+newUser);

        // Save the user entity in the database
        User savedUser = userRepository.save(newUser);

        // Generate access token and refresh token
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Store the refresh token in the database (if necessary)
        savedUserToken(savedUser, accessToken);

//        System.out.println("--------------------saved tokens");

        // Build the authentication response
        AuthenticationResponse response = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return response;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate request credentials
        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            throw new UnauthorizedException("Invalid Credentials!!");
        }

        // Generate Jwt Tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        savedUserToken(user, accessToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void savedUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }


    private void revokeAllUserTokens(com.bootcamp.bookrentalsystem.model.User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    public ResponseEntity<String> validateToken(String token, UUID userId) {

        if (!jwtService.isValidUserToken(token, userId)) {
            throw new UnauthorizedException("Invalid Token");
        }
        return ResponseEntity.ok("Token is valid");
    }

    public ResponseEntity<String> refreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Token required");
        }

        if (!refreshToken.startsWith("Bearer ")) {
            throw new BadRequestException("Invalid token format");
        }

        String jwtToken = refreshToken.substring(7);

        Claims claims = jwtService.decodeToken(jwtToken);

        // Get token expiration
        Date expiration = claims.getExpiration();

//        System.out.println("-------------------------IS TOKEN NOT EXPIRED: " + jwtService.isTokenNotExpired(expiration));
        // Check if the refresh token is valid and not expired
        if (jwtService.isTokenNotExpired(expiration)) {

            // Get the necessary user details from the refresh token, such as user ID or email
            String email = claims.get("email", String.class);

            // Retrieve the user from the database based on the email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Generate a new access token for the user
            String newAccessToken = jwtService.generateToken(user);

//            System.out.println("----------------TOKEN EMAIL: " + email);
//            System.out.println("----------------USER FOUND WITH EMAIL: " + user.getEmail());
//            System.out.println("----------------NEW ACCESS TOKEN: " + newAccessToken);

            // Perform any additional logic, such as revoking old tokens
            revokeAllUserTokens(user);
            savedUserToken(user, newAccessToken);

            return ResponseEntity.ok(newAccessToken);
        } else {
            // Handle invalid or expired refresh token
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
    }

}