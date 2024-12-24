package com.wnc.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "dept_reminder")
public class DeptReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "senderAccountId", nullable = false)
    private Account senderAccountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiverAccountId", nullable = false)
    private Account receiverAccountId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    private String status;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
}