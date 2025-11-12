package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.*;
import com.pikel.balancetracker.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BalanceTrackerServiceTest {

    private BalanceTrackerService balanceTrackerService;

    @BeforeEach
    void setUp() {
        balanceTrackerService = new BalanceTrackerService();
    }

    @Test
    void getBalanceSummary() {
        LocalDate testDate = LocalDate.of(2025, 11, 14);
        Transaction debt1 = new Transaction(
                "Car Loan",
                100.0,
                testDate.plusDays(3),
                TransactionType.DEBT,
                PayPeriod.MONTHLY
        );
        Transaction income1 = new Transaction(
                "Salary",
                500.0,
                DateUtils.findNext1stOr15th(testDate),
                TransactionType.INCOME,
                PayPeriod.SEMIMONTHLY
        );
        BalanceDataRequest request = new BalanceDataRequest(
                List.of(debt1, income1),
                1000.0,
                ProjectionTimeframe.ONE_YEAR,
                SummarizeDateBy.DAY
        );

        List<DatedBalance> res = balanceTrackerService.getBalanceSummary(request);

        assertNotNull(res);
        assertFalse(res.isEmpty());

        // assert initial balance
        assertEquals(1000.0, res.getFirst().balance());

        // assert balance on first income pay date
        LocalDate incomeDate = income1.date();
        DatedBalance incomeBalance = res.stream()
                .filter(datedBalance -> datedBalance.date().equals(incomeDate))
                .findFirst()
                .orElseThrow();
        assertEquals(1500.0, incomeBalance.balance());

        // assert balance on first debt due date
        LocalDate debtDate = debt1.date();
        DatedBalance debtBalance = res.stream()
                .filter(datedBalance -> datedBalance.date().equals(debtDate))
                .findFirst()
                .orElseThrow();
        assertEquals(1400.0, debtBalance.balance());
    }
}