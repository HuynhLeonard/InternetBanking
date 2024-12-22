package com.wnc.banking.service;

import com.wnc.banking.dto.ServiceProviderDto;
import com.wnc.banking.entity.Role;
import com.wnc.banking.entity.ServiceProvider;
import com.wnc.banking.repository.ServiceProviderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public String createServiceProvider(ServiceProviderDto serviceProviderDto) {
        ServiceProvider serviceProvider = new ServiceProvider();

        serviceProvider.setName(serviceProviderDto.getName());
        serviceProvider.setEmail(serviceProviderDto.getEmail());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        serviceProvider.setPassword(bCryptPasswordEncoder.encode(serviceProviderDto.getPassword()));

        serviceProvider.setRole(Role.employee);
        serviceProvider.setPhoneNumber(serviceProviderDto.getPhoneNumber());
        serviceProvider.setAddress(serviceProviderDto.getAddress());
        serviceProvider.setCreatedAt(LocalDateTime.now());
        serviceProvider.setUpdatedAt(LocalDateTime.now());

        serviceProviderRepository.save(serviceProvider);
        return "Create employee successfully";
    }

    public String updateServiceProvider(String email, ServiceProviderDto serviceProviderDto) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByEmail(email);
        if (serviceProvider == null) {
            return "Cannot found employee with email: " + email;
        }

        if (serviceProviderDto.getName() != null && !serviceProviderDto.getName().isEmpty()) {
            serviceProvider.setName(serviceProviderDto.getName());
        }

        if (serviceProviderDto.getEmail() != null && !serviceProviderDto.getEmail().isEmpty()) {
            serviceProvider.setEmail(serviceProviderDto.getEmail());
        }

        if (serviceProviderDto.getPhoneNumber() != null && !serviceProviderDto.getPhoneNumber().isEmpty()) {
            serviceProvider.setPhoneNumber(serviceProviderDto.getPhoneNumber());
        }

        if (serviceProviderDto.getAddress() != null && !serviceProviderDto.getAddress().isEmpty()) {
            serviceProvider.setAddress(serviceProviderDto.getAddress());
        }

        serviceProvider.setUpdatedAt(LocalDateTime.now());
        serviceProviderRepository.save(serviceProvider);

        return "Update employee successfully";
    }
}
