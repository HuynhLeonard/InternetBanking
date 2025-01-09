package com.wnc.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllTransactionResponse {
    String sendBank;
    String receiveBank;
    String amount;
    String sendingAccount;
    String receivingAccount;
    String date;
}
