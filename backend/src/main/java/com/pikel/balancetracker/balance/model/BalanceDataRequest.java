package com.pikel.balancetracker.balance.model;

import java.time.LocalDate;
import java.util.List;

public record BalanceDataRequest(
        List<Transaction> transactions,
        double currentBalance,
        ProjectionTimeframe projectionTimeframe,
        SummarizeDateBy summarizeDateBy,
        LocalDate startDate
) {}
