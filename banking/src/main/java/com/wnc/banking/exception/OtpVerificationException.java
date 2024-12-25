package com.wnc.banking.exception;

public class OtpVerificationException extends RuntimeException {
    public OtpVerificationException(String message) {
        super(message);
    }
}