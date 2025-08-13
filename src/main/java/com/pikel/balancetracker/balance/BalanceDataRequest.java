package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.ProjectionTimeframe;
import com.pikel.balancetracker.balance.model.SummarizeDateBy;
import com.pikel.balancetracker.balance.model.Transaction;

import java.util.List;

public record BalanceDataRequest(
        List<Transaction> transactions,
        double currentBalance,
        ProjectionTimeframe projectionTimeframe,
        SummarizeDateBy summarizeDateBy
) {}
