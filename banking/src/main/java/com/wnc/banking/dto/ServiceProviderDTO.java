package com.wnc.banking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class ServiceProviderDTO {
    @Schema(example = "Service Provider")
    @NotNull(message = "Name is required", groups = OnCreateDTO.class)
    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    private String name;

    @Schema(example = "serviceprovider@gmail.com")
    @NotNull(message = "Email is required", groups = OnCreateDTO.class)
    @Email(message = "Invalid email address", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    private String email;

    @Schema(example = "password")
    @NotNull(message = "Password is required", groups = OnCreateDTO.class)
    @Size(min = 8, max = 24, message = "Password must be between 8 and 24 characters", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Schema(example = "admin")
    @NotNull(message = "Role is required")
    private String role;

    @Schema(example = "0123456789")
    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must have 10 digits and start with 0", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    private String phoneNumber;

    @Schema(example = "123 ABC street, DEF ward, GHI city")
    @Size(min = 10, max = 100, message = "Address must be between 10 and 100 characters", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    private String address;

    public ServiceProviderDTO(String name, String email, String role, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
