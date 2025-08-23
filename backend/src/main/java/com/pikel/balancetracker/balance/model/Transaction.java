package com.pikel.balancetracker.balance.model;

import java.time.LocalDate;

public record Transaction(
        String sourceName,
        double amount,
        LocalDate date, // user inputs soonest date of payment
        TransactionType type, // debt or income
        PayPeriod payPeriod
) {
    public Transaction withDate(LocalDate newDate) {
        return new Transaction(sourceName, amount, newDate, type, payPeriod);
    }
}
