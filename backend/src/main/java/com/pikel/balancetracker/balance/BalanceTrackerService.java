package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.DatedBalance;
import com.pikel.balancetracker.balance.model.SummarizeDateBy;
import com.pikel.balancetracker.balance.model.Transaction;
import com.pikel.balancetracker.balance.model.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class BalanceTrackerService {
    /**
     * return a list of balances based on our selected timeframe and summarize date by values
     *
     * @param request from with lists of debts, incomes, current balance, etc
     * @return full balance summary
     */
    public List<DatedBalance> getBalanceSummary(BalanceDataRequest request) {
        PriorityQueue<Transaction> transactionQueue = new PriorityQueue<>(Comparator.comparing(Transaction::date));
        transactionQueue.addAll(request.transactions());

        SummarizeDateBy summarizeDateBy = request.summarizeDateBy();
        double currBalance = request.currentBalance();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(request.projectionTimeframe().getYears());

        List<DatedBalance> outputBalanceList = new ArrayList<>();

        LocalDate currDate = startDate;
        int step = 0;
        while (!currDate.isAfter(endDate)) {
            currBalance = processQueue(transactionQueue, currDate, currBalance);
            outputBalanceList.add(new DatedBalance(currBalance, currDate));
            currDate = incrementDate(startDate, ++step, summarizeDateBy);
        }
        return outputBalanceList;
    }

    private double processQueue(
            PriorityQueue<Transaction> queue,
            LocalDate currDate,
            double currBalance
    ) {
        if (queue.isEmpty()) return currBalance;

        while (!queue.isEmpty() && (!queue.peek().date().isAfter(currDate))) {
            Transaction currTransaction = queue.poll();
            if (currTransaction != null) {
                double amount = currTransaction.amount();
                currBalance += currTransaction.type() == TransactionType.INCOME ? amount : -amount;
                // since date is always updated here we just step once
                Transaction newTransaction = currTransaction.withDate(
                        incrementDate(currDate, 1, SummarizeDateBy.MONTH)
                );
                queue.add(newTransaction);
            }
        }
        return currBalance;
    }

    private LocalDate incrementDate(LocalDate date, int step, SummarizeDateBy summarizeDateBy) {
        return switch (summarizeDateBy) {
            case DAY -> date.plusDays(step);
            case WEEK -> date.plusWeeks(step);
            case MONTH -> date.plusMonths(step);
        };
    }
}
