package com.pikel.balancetracker;

import java.time.LocalDate;

public class Income {
    private String name;
    private double amount;
    private LocalDate payDate;

    public Income() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getPayDate() {
        return payDate;
    }

    public void setPayDate(LocalDate payDate) {
        this.payDate = payDate;
    }
}
