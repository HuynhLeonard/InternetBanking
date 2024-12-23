package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.OnCreateDto;
import com.wnc.banking.dto.OnUpdateDto;
import com.wnc.banking.dto.ServiceProviderDto;
import com.wnc.banking.service.ServiceProviderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/protected/service-provider")
@AllArgsConstructor
public class ServiceProviderController {
    private final ServiceProviderService serviceProviderService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ServiceProviderDto>>> getAllServiceProviders() {
        try {
            List<ServiceProviderDto> serviceProviders = serviceProviderService.getAllServiceProviders();
            if (serviceProviders != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of("Get all employees successfully"), serviceProviders));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, List.of("Cannot find any employees"), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @PostMapping()
    ResponseEntity<ApiResponse<String>> createEmployee(@RequestBody @Validated(OnCreateDto.class) ServiceProviderDto serviceProviderDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        }
        try {
            String message = serviceProviderService.createServiceProvider(serviceProviderDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, List.of(message), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @PatchMapping("update-employee/{email}")
    ResponseEntity<ApiResponse<String>> updateEmployee(@PathVariable String email, @RequestBody @Validated(OnUpdateDto.class) ServiceProviderDto serviceProviderDto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, errors, null));
        }
        try {
            String message = serviceProviderService.updateServiceProvider(email, serviceProviderDto);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(message), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }

    @DeleteMapping("delete-employee/{email}")
    ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable String email) {
        try {
            String message = serviceProviderService.deleteServiceProvider(email);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, List.of(message), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, List.of(e.getMessage()), null));
        }
    }
}
