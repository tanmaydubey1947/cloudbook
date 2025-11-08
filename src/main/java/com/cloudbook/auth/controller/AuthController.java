package com.cloudbook.auth.controller;

import com.cloudbook.auth.dto.AuthRequest;
import com.cloudbook.auth.dto.UserRequest;
import com.cloudbook.auth.service.auth.AuthService;
import com.cloudbook.auth.service.user.UserService;
import com.cloudbook.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Generate Token")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Token generated successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody final AuthRequest request) {
        log.info("Initiating token generation...");
        final BaseResponse response = authService.authenticate(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Register User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User Registered successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody final UserRequest request) {
        log.info("Initiating user registration...");
        final BaseResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
