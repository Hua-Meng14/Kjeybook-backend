package com.bootcamp.bookrentalsystem.auth;

import com.bootcamp.bookrentalsystem.model.RegisterUser;
import com.bootcamp.bookrentalsystem.model.User;
import com.sun.net.httpserver.HttpsServer;
import lombok.RequiredArgsConstructor;
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
//    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        authenticationService.refreshToken(request, response);
//    }

    @PostMapping("/validate-token")
    public ResponseEntity validateToken(@RequestParam("token") String token) {
        return ResponseEntity.ok(authenticationService.validateToken(token));
    }

}
