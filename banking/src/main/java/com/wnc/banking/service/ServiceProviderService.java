package com.wnc.banking.service;

import com.wnc.banking.dto.ServiceProviderDto;
import com.wnc.banking.entity.Role;
import com.wnc.banking.entity.ServiceProvider;
import com.wnc.banking.repository.ServiceProviderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ServiceProviderService {
    private final ServiceProviderRepository serviceProviderRepository;

    public List<ServiceProviderDto> getAllServiceProviders() {
        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findByNameNotIn("Admin");
        return serviceProviderList.stream()
                .map(serviceProvider -> new ServiceProviderDto(
                        serviceProvider.getName(),
                        serviceProvider.getEmail(),
                        serviceProvider.getRole(),
                        serviceProvider.getPhoneNumber(),
                        serviceProvider.getAddress()))
                .collect(Collectors.toList());
    }
}
