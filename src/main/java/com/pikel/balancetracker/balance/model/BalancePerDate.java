package com.pikel.balancetracker.balance.model;

import java.time.LocalDate;

public class BalancePerDate {
    private double balance;
    private LocalDate date;

    public BalancePerDate(double balance, LocalDate date) {
        this.balance = balance;
        this.date = date;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
