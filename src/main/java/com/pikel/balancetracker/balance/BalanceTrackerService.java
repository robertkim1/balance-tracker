package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.BalancePerDate;
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
    public List<BalancePerDate> getBalanceSummary(BalanceDataRequest request) {
        List<Debt> debts = request.getDebts();
        List<Income> incomes = request.getIncomes();
        PriorityQueue<Debt> debtQueue = new PriorityQueue<>(Comparator.comparing(Debt::getDueDate));
        PriorityQueue<Income> incomeQueue = new PriorityQueue<>(Comparator.comparing(Income::getPayDate));
        debtQueue.addAll(debts);
        incomeQueue.addAll(incomes);

        SummarizeDateBy summarizeDateBy = request.getSummarizeDateBy();
        double currBalance = request.getCurrentBalance();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(request.getProjectionTimeframe().getYears());

        int numOfDataPoints = switch (summarizeDateBy) {
            case DAY -> (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
            case WEEK -> (int) Math.ceil((double) ChronoUnit.DAYS.between(startDate, endDate) / 7);
            case MONTH -> (int) ChronoUnit.MONTHS.between(startDate, endDate);
        };

        List<BalancePerDate> outputBalanceList = new ArrayList<>();
        outputBalanceList.add(new BalancePerDate(currBalance, startDate));
        for (int i = 0; i < numOfDataPoints; i++) {
            // TODO: gotta check this later
            LocalDate currDate = switch (summarizeDateBy) {
                case DAY -> startDate.plusDays(i);
                case WEEK -> startDate.plusWeeks(i);
                case MONTH -> startDate.plusMonths(i);
            };
            Debt debtPeek = debtQueue.peek();
            if (debtPeek != null && (currDate.isEqual(debtPeek.getDueDate()) || currDate.isAfter(debtPeek.getDueDate()))) {
                Debt debtPoll = debtQueue.poll();
                if (debtPoll != null) {
                    currBalance -= debtPoll.getAmount();
                    LocalDate debtPollDate = debtPoll.getDueDate();
                    LocalDate newDueDate = switch (summarizeDateBy) {
                        case DAY -> debtPollDate.plusDays(i);
                        case WEEK -> debtPollDate.plusWeeks(i);
                        case MONTH -> debtPollDate.plusMonths(i);
                    };
                    debtPoll.setDueDate(newDueDate);
                    debtQueue.add(debtPoll);
                }
            }
            Income incomePeek = incomeQueue.peek();
            if (incomePeek != null && (currDate.isEqual(incomePeek.getPayDate()) || currDate.isAfter(incomePeek.getPayDate()))) {
                Income incomePoll = incomeQueue.poll();
                if (incomePoll != null) {
                    currBalance += incomePoll.getAmount();
                    LocalDate incomePollDate = incomePoll.getPayDate();
                    LocalDate newPayDate = switch (summarizeDateBy) {
                        case DAY -> incomePollDate.plusDays(i);
                        case WEEK -> incomePollDate.plusWeeks(i);
                        case MONTH -> incomePollDate.plusMonths(i);
                    };
                    incomePoll.setPayDate(newPayDate);
                    incomeQueue.add(incomePoll);
                }
            }
            outputBalanceList.add(new BalancePerDate(currBalance, startDate));
        }
        return outputBalanceList;
    }
}
