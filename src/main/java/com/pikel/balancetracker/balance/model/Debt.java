package com.pikel.balancetracker.balance.model;

import java.time.LocalDate;

public class Debt {
    private String source;
    private double amount;
    // user inputs soonest date of payment
    private LocalDate dueDate;

    public Debt() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
