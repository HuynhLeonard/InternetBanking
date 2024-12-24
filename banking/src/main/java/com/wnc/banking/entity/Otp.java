package com.wnc.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "otp", nullable = false)
    private String otp;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "expiredAt")
    private LocalDateTime expiredAt;

}