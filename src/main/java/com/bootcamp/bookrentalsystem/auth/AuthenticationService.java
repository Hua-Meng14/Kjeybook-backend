package com.bootcamp.bookrentalsystem.auth;

import com.bootcamp.bookrentalsystem.exception.BadRequestException;
import com.bootcamp.bookrentalsystem.exception.ForbiddenException;
import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.exception.UnauthorizedException;
import com.bootcamp.bookrentalsystem.model.RegisterUser;
import com.bootcamp.bookrentalsystem.model.Token;
import com.bootcamp.bookrentalsystem.model.TokenType;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.TokenRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import com.bootcamp.bookrentalsystem.service.JwtService;
import com.bootcamp.bookrentalsystem.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterUser registerUser) {
        String username = registerUser.getUsername();
        String email = registerUser.getEmail();
        String password = registerUser.getPassword();
        String role = registerUser.getRole();

        // Check if the username or email already exists in the database
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        // Create a new user entity
        User newUser = new User(username, email, passwordEncoder.encode(password), role);

        // Save the user entity in the database
        User savedUser = userRepository.save(newUser);

        // Generate access token and refresh token
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Store the refresh token in the database (if necessary)
        savedUserToken(savedUser, accessToken);

        System.out.println("--------------------saved tokens");

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
        var validUserTokens = tokenRepository.findAllValidTokenByUser(Math.toIntExact(user.getUserId()));
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    public ResponseEntity<String> validateToken(String token, Long userId) {

        if (!jwtService.isValidUserToken(token, userId)) {
            throw new UnauthorizedException("Invalid Token");
        }
        return ResponseEntity.ok("Token is valid");
    }

//    public ResponseEntity<String> refreshToken(String refreshToken) {
//        // Perform token refresh logic here
//        // Assuming the logic to generate a new access token based on the refresh token
////        System.out.println("-------------------------IS TOKEN EXPIRED: " + jwtService.isTokenExpired(refreshToken));
//        // Check if the refresh token is valid and not expired
//        if (jwtService.isTokenExpired(refreshToken)) {
//            // Get the necessary user details from the refresh token, such as user ID or email
//            String email = jwtService.decodeToken(refreshToken).get("email", String.class);
////            System.out.println("----------------TOKEN EMAIL: " + email);
//            // Retrieve the user from the database based on the email
//            User user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
////            System.out.println("----------------USER FOUND WITH EMAIL: " + user.getEmail());
//
//            // Generate a new access token for the user
//            String newAccessToken = jwtService.generateToken(user);
////            System.out.println("----------------NEW ACCESS TOKEN: " + newAccessToken);
//
//            // Perform any additional logic, such as revoking old tokens
//            revokeAllUserTokens(user);
//            savedUserToken(user, newAccessToken);
//
//            return ResponseEntity.ok(newAccessToken);
//        } else {
//            // Handle invalid or expired refresh token
//            throw new UnauthorizedException("Invalid or expired refresh token");
//        }
//    }

}