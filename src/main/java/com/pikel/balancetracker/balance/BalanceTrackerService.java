package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.DatedBalance;
import com.pikel.balancetracker.balance.model.Debt;
import com.pikel.balancetracker.balance.model.Income;
import com.pikel.balancetracker.balance.model.SummarizeDateBy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class BalanceTrackerService {
    /**
     * return a list of balances based on our selected timeframe and summarize date by values
     * @param request from with lists of debts, incomes, current balance, etc
     * @return full balance summary
     */
    public List<DatedBalance> getBalanceSummary(BalanceDataRequest request) {
        PriorityQueue<Debt> debtQueue = new PriorityQueue<>(Comparator.comparing(Debt::getDueDate));
        PriorityQueue<Income> incomeQueue = new PriorityQueue<>(Comparator.comparing(Income::getPayDate));
        debtQueue.addAll(request.getDebts());
        incomeQueue.addAll(request.getIncomes());

        SummarizeDateBy summarizeDateBy = request.getSummarizeDateBy();
        double currBalance = request.getCurrentBalance();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(request.getProjectionTimeframe().getYears());

        int numOfDataPoints = switch (summarizeDateBy) {
            case DAY -> (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
            case WEEK -> (int) Math.ceil((double) ChronoUnit.DAYS.between(startDate, endDate) / 7);
            case MONTH -> (int) ChronoUnit.MONTHS.between(startDate, endDate);
        };

        List<DatedBalance> outputBalanceList = new ArrayList<>();
        outputBalanceList.add(new DatedBalance(currBalance, startDate));

        for (int i = 0; i < numOfDataPoints; i++) {
            LocalDate currDate = incrementDate(startDate, i, summarizeDateBy);
            currBalance = processQueue(
                    debtQueue, currDate, currBalance, summarizeDateBy, i, false
            );
            currBalance = processQueue(
                    incomeQueue, currDate, currBalance, summarizeDateBy, i, true
            );
            outputBalanceList.add(new DatedBalance(currBalance, currDate));
        }
        return outputBalanceList;
    }

    private <T> double processQueue(
            PriorityQueue<T> queue,
            LocalDate currDate,
            double currBalance,
            SummarizeDateBy summarizeDateBy,
            int step,
            boolean isIncome
    ) {
        if (queue.isEmpty()) return currBalance;

        LocalDate date = isIncome
                ? ((Income) queue.peek()).getPayDate()
                : ((Debt) queue.peek()).getDueDate();

        if (currDate.isBefore(date)) return currBalance;

        T item = queue.poll();
        if (item != null) {
            double amount = isIncome
                    ? ((Income) item).getAmount()
                    : ((Debt) item).getAmount();

            currBalance += isIncome ? amount : -amount;

            LocalDate newDate = incrementDate(date, step, summarizeDateBy);
            if (isIncome) {
                ((Income) item).setPayDate(newDate);
            } else {
                ((Debt) item).setDueDate(newDate);
            }
            queue.add(item);
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
