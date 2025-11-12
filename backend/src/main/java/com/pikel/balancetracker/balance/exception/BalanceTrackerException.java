package com.pikel.balancetracker.balance.exception;

public class BalanceTrackerException extends RuntimeException {
    public BalanceTrackerException(String message) {
        super(message);
    }

    public BalanceTrackerException(String message, Throwable cause) {
        super(message, cause);
    }
}