package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.AuthResponse;
import com.wnc.banking.dto.LoginRequest;
import com.wnc.banking.dto.RefreshTokenRequest;
import com.wnc.banking.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                AuthResponse response = authService.login(request.getEmail(), request.getPassword());
                if (response.getAccessToken() == null && response.getRefreshToken() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of(response.getMessage()), null));
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(response.getMessage()), response));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        }
    }

    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<AuthResponse>> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        } else {
            try {
                AuthResponse response = authService.refreshAccessToken(request.getRefreshToken());
                if (response.getAccessToken() == null && response.getRefreshToken() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, List.of(response.getMessage()), null));
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(response.getMessage()), response));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
            }
        }
    }
}
