package com.pikel.balancetracker;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceTrackerService {

    public List<Double> getBalanceSummary(BalanceDataRequest request) {
        // return a list of balances based on our selected timeframe and summarize date by values
        List<Debt> debts = request.getDebts();
        List<Income> incomes = request.getIncomes();
        int totalDataPoints = request.getProjectionTimeframe()

        return null;
    }
}
