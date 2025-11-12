package com.pikel.balancetracker.balance.model;

import java.util.List;

public record DataPoint (double balance,
                         List<Transaction> transactionList) {}
