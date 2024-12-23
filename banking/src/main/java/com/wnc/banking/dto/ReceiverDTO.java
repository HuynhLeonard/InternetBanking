package com.wnc.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiverDTO {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String nickName;
}
