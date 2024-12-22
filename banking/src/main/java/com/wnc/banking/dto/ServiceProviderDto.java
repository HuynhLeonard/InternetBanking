package com.wnc.banking.dto;

import com.wnc.banking.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ServiceProviderDto {
    @NotNull(message = "Name is required", groups = OnCreateDto.class)
    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters", groups = {OnCreateDto.class, OnUpdateDto.class})
    private String name;

    @NotNull(message = "Email is required", groups = OnCreateDto.class)
    @Email(message = "Invalid email address", groups = {OnCreateDto.class, OnCreateDto.class})
    private String email;

    @NotNull(message = "Password is required", groups = OnCreateDto.class)
    @Size(min = 8, max = 24, message = "Password must be between 8 and 24 characters", groups = {OnCreateDto.class, OnUpdateDto.class})
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must have 10 digits and start with 0", groups = {OnCreateDto.class, OnUpdateDto.class})
    private String phoneNumber;

    @Size(min = 10, max = 100, message = "Address must be between 10 and 100 characters", groups = {OnCreateDto.class, OnUpdateDto.class})
    private String address;
}
