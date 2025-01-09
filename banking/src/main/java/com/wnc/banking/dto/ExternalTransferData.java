package com.wnc.banking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalTransferData {
    private String data;
    private String signedData;
}
