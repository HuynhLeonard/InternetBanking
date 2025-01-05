package com.wnc.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    @NotNull(message = "Name is required", groups = OnCreateDTO.class)
    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    private String name;

    @NotNull(message = "Email is required", groups = OnCreateDTO.class)
    @Email(message = "Invalid email address", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    private String email;

    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must have 10 digits and start with 0", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    private String phoneNumber;

    @Size(min = 10, max = 100, message = "Address must be between 10 and 100 characters", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    private String address;

    @NotNull(message = "Password is required", groups = OnCreateDTO.class)
    @Size(min = 8, max = 24, message = "Password must be between 8 and 24 characters", groups = {OnCreateDTO.class, OnUpdateDTO.class})
    private String password;
}
