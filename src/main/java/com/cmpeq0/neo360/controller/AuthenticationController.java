package com.cmpeq0.neo360.controller;

import com.cmpeq0.neo360.view.auth.AuthenticationRequest;
import com.cmpeq0.neo360.view.auth.AuthenticationResponse;
import com.cmpeq0.neo360.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authRequest) {
            return ResponseEntity.ok(service.authenticateRole(authRequest));
    }


}
