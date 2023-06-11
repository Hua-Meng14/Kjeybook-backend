package com.bootcamp.bookrentalsystem.auth;

import com.bootcamp.bookrentalsystem.exception.UnauthorizedException;
import com.bootcamp.bookrentalsystem.model.RegisterUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterUser registerUser
    ) {
        return ResponseEntity.ok(authenticationService.register(registerUser));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity refreshToken(@RequestParam("refreshToken") String refreshToken) {
//
//        if (refreshToken == null || refreshToken.isEmpty()) {
//            throw new UnauthorizedException("Refresh token not provided");
//        }
//
//        ResponseEntity<String> newAccessToken = authenticationService.refreshToken(refreshToken);
//        if (newAccessToken != null) {
//            return ResponseEntity.ok(newAccessToken);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to refresh token");
//        }
//    }

    @PostMapping("/validate-token/{userId}")
    public ResponseEntity validateToken(@RequestHeader("Authorization") String token, @PathVariable Long userId) {
        return ResponseEntity.ok(authenticationService.validateToken(token, userId));
    }

}
