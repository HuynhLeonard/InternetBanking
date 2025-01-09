package com.wnc.banking.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponsePartnerTeam3<T> {
    private Boolean success;
    private T data;
    private List<Team3Errors> errors;
}
