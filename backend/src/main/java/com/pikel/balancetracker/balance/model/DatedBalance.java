package com.pikel.balancetracker.balance.model;

import java.time.LocalDate;

public record DatedBalance(
        double balance,
        LocalDate date
) {}