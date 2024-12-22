package com.wnc.banking.controller;

import com.wnc.banking.dto.ApiResponse;
import com.wnc.banking.dto.ServiceProviderDto;
import com.wnc.banking.entity.ServiceProvider;
import com.wnc.banking.service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/service-provider")
public class ServiceProviderController {
    private final ServiceProviderService serviceProviderService;

    @Autowired
    public ServiceProviderController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

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
}
