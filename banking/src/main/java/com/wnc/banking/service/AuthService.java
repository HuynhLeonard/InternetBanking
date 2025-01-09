package com.wnc.banking.service;

import com.wnc.banking.dto.AuthResponse;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.entity.RefreshToken;
import com.wnc.banking.entity.ServiceProvider;
import com.wnc.banking.repository.CustomerRepository;
import com.wnc.banking.repository.RefreshTokenRepository;
import com.wnc.banking.repository.ServiceProviderRepository;
import com.wnc.banking.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final CustomerRepository customerRepository;
    private final ServiceProviderRepository serviceProviderRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse login(String email, String password) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            ServiceProvider serviceProvider = serviceProviderRepository.findByEmail(email);

            if (serviceProvider == null) {
                return new AuthResponse(null, null, "Cannot found user with email: " + email);
            } else {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                if (!passwordEncoder.matches(password, serviceProvider.getPassword())) {
                    return new AuthResponse(null, null, "Invalid password");
                }

                String accessToken = jwtTokenProvider.generateAccessToken(email, serviceProvider.getRole());
                String refreshToken = jwtTokenProvider.generateRefreshToken();

                RefreshToken refreshTokenEntity = new RefreshToken();
                refreshTokenEntity.setRefreshToken(refreshToken);
                refreshTokenEntity.setOwnerId(serviceProvider.getId());

                LocalDateTime now = LocalDateTime.now();
                refreshTokenEntity.setCreatedAt(now);
                refreshTokenEntity.setExpiredAt(now.plusSeconds(jwtTokenProvider.getREFRESH_EXPIRATION()));
                refreshTokenRepository.save(refreshTokenEntity);

                return new AuthResponse(accessToken, refreshToken, "Successfully logged in");
            }
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(password, customer.getPassword())) {
                return new AuthResponse(null, null, "Invalid password");
            }

            String accessToken = jwtTokenProvider.generateAccessToken(email, "customer");
            String refreshToken = jwtTokenProvider.generateRefreshToken();

            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setRefreshToken(refreshToken);
            refreshTokenEntity.setOwnerId(customer.getId());

            LocalDateTime now = LocalDateTime.now();
            refreshTokenEntity.setCreatedAt(now);
            refreshTokenEntity.setExpiredAt(now.plusSeconds(jwtTokenProvider.getREFRESH_EXPIRATION()));
            refreshTokenRepository.save(refreshTokenEntity);

            return new AuthResponse(accessToken, refreshToken, "Successfully logged in");
        }
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (refreshTokenEntity == null) {
            return  new AuthResponse(null, null, "Invalid refresh token");
        }

        if (refreshTokenEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            return  new AuthResponse(null, null, "Refresh token expired");
        }

        Optional<Customer> customer = customerRepository.findById(refreshTokenEntity.getOwnerId());
        if (customer.isPresent()) {
            String accessToken = jwtTokenProvider.generateAccessToken(customer.get().getEmail(), "customer");
            return new AuthResponse(accessToken, refreshToken, "Successfully refresh access token");
        } else {
            Optional<ServiceProvider> serviceProvider = serviceProviderRepository.findById(refreshTokenEntity.getOwnerId());

            if (serviceProvider.isPresent()) {
                String accessToken = jwtTokenProvider.generateAccessToken(serviceProvider.get().getEmail(), serviceProvider.get().getRole());
                return new AuthResponse(accessToken, refreshToken, "Successfully refresh access token");
            } else {
                return new AuthResponse(null, null, "Cannot found user with id: " + refreshTokenEntity.getOwnerId());
            }
        }
    }
}
