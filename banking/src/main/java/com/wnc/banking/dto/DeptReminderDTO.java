package com.wnc.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeptReminderDTO {
    private String receiverAccountNumber;
    private Long amount;
    private String message;
}
