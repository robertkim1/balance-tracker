package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.*;
import com.pikel.balancetracker.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BalanceEngineTest {

    private BalanceEngine balanceEngine;

    @BeforeEach
    void setUp() {
        balanceEngine = new BalanceEngine();
    }

    private DataPointPerDate findBalanceOnDate(List<DataPointPerDate> balances, LocalDate date) {
        return balances.stream()
                .filter(b -> b.date().equals(date))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No balance found for date: " + date));
    }

    // TODO: test validation

    @Test
    void getBalanceSummaryOneDebtOneIncomeTransaction() {
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
                SummarizeDateBy.DAY,
                testDate
        );

        List<DataPointPerDate> res = balanceEngine.getBalanceSummary(request);

        assertNotNull(res);
        assertFalse(res.isEmpty());

        // assert initial balance
        assertEquals(1000.0, findBalanceOnDate(res, testDate).dataPoint().balance());

        // assert balance on first income pay date
        assertEquals(1500.0, findBalanceOnDate(res, testDate.plusDays(1)).dataPoint().balance());

        // assert balance on first debt due date
        assertEquals(1400.0, findBalanceOnDate(res, testDate.plusDays(3)).dataPoint().balance());
    }

    @Test
    void getBalanceSummaryMultipleTransactions() {
        LocalDate testDate = LocalDate.of(2025, 1, 1); // Starting on a Wednesday

        // Create multiple income sources
        Transaction salary = new Transaction(
                "Salary",
                3000.0,
                testDate, // Jan 1st
                TransactionType.INCOME,
                PayPeriod.SEMIMONTHLY
        );

        Transaction freelance = new Transaction(
                "Freelance Work",
                800.0,
                testDate.plusDays(10), // Jan 11th
                TransactionType.INCOME,
                PayPeriod.MONTHLY
        );

        Transaction sideGig = new Transaction(
                "Side Business",
                500.0,
                testDate.plusDays(7), // Jan 8th
                TransactionType.INCOME,
                PayPeriod.WEEKLY
        );

        // Create multiple debt sources
        Transaction rent = new Transaction(
                "Rent",
                1500.0,
                testDate.plusDays(1), // Jan 2nd
                TransactionType.DEBT,
                PayPeriod.MONTHLY
        );

        Transaction carLoan = new Transaction(
                "Car Loan",
                350.0,
                testDate.plusDays(5), // Jan 6th
                TransactionType.DEBT,
                PayPeriod.MONTHLY
        );

        Transaction utilities = new Transaction(
                "Utilities",
                150.0,
                testDate.plusDays(15), // Jan 16th
                TransactionType.DEBT,
                PayPeriod.MONTHLY
        );

        Transaction groceries = new Transaction(
                "Groceries",
                200.0,
                testDate.plusDays(3), // Jan 4th (every Saturday)
                TransactionType.DEBT,
                PayPeriod.WEEKLY
        );

        Transaction subscriptions = new Transaction(
                "Subscriptions",
                50.0,
                testDate.plusDays(2), // Jan 3rd
                TransactionType.DEBT,
                PayPeriod.SEMIMONTHLY
        );

        BalanceDataRequest request = new BalanceDataRequest(
                List.of(salary, freelance, sideGig, rent, carLoan, utilities, groceries, subscriptions),
                5000.0, // Starting balance
                ProjectionTimeframe.ONE_YEAR,
                SummarizeDateBy.DAY,
                testDate
        );

        List<DataPointPerDate> res = balanceEngine.getBalanceSummary(request);

        assertNotNull(res);
        assertFalse(res.isEmpty());

        // Assert initial balance (Jan 1)
        DataPointPerDate jan1 = findBalanceOnDate(res, testDate);
        assertEquals(8000.0, jan1.dataPoint().balance(), "Initial balance + salary: 5000 + 3000 = 8000");

        // After rent (Jan 2)
        DataPointPerDate jan2 = findBalanceOnDate(res, testDate.plusDays(1));
        assertEquals(6500.0, jan2.dataPoint().balance(), "After rent: 8000 - 1500 = 6500");

        // After subscriptions (Jan 3)
        DataPointPerDate jan3 = findBalanceOnDate(res, testDate.plusDays(2));
        assertEquals(6450.0, jan3.dataPoint().balance(), "After subscriptions: 6500 - 50 = 6450");

        // After groceries (Jan 4)
        DataPointPerDate jan4 = findBalanceOnDate(res, testDate.plusDays(3));
        assertEquals(6250.0, jan4.dataPoint().balance(), "After groceries: 6450 - 200 = 6250");

        // After car loan (Jan 6)
        DataPointPerDate jan6 = findBalanceOnDate(res, testDate.plusDays(5));
        assertEquals(5900.0, jan6.dataPoint().balance(), "After car loan: 6250 - 350 = 5900");

        // After side gig (Jan 8)
        DataPointPerDate jan8 = findBalanceOnDate(res, testDate.plusDays(7));
        assertEquals(6400.0, jan8.dataPoint().balance(), "After side gig: 5900 + 500 = 6400");

        // After freelance (Jan 11) - also groceries repeats on Jan 11
        DataPointPerDate jan11 = findBalanceOnDate(res, testDate.plusDays(10));
        assertEquals(7000.0, jan11.dataPoint().balance(), "After freelance and groceries: 6400 + 800 - 200 = 7000");

        // After salary again (Jan 15)
        DataPointPerDate jan15 = findBalanceOnDate(res, testDate.plusDays(14));
        assertEquals(10450.0, jan15.dataPoint().balance(), "After second salary, subscription repeat, side gig: 7000 + 3000 - 50 + 500 = 10450");

        // After utilities (Jan 16) - also side gig repeats (weekly from Jan 8)
        DataPointPerDate jan16 = findBalanceOnDate(res, testDate.plusDays(15));
        assertEquals(10300.0, jan16.dataPoint().balance(), "After utilities: 10450 - 150 = 10300");

        // After groceries (Jan 18)
        DataPointPerDate jan18 = findBalanceOnDate(res, testDate.plusDays(17));
        assertEquals(10100.0, jan18.dataPoint().balance(), "After groceries: 10300 - 200 = 10100");

        // Check end of first month (Jan 31)
        DataPointPerDate jan31 = findBalanceOnDate(res, testDate.plusDays(30));
        assertTrue(jan31.dataPoint().balance() > 10000.0, "Balance should have grown after first month");

        // Monthly net calculation:
        // Income per month: (3000 * 2) + 800 + (500 * 4.33) ≈ 8965
        // Expenses per month: 1500 + 350 + 150 + (200 * 4.33) + (50 * 2) ≈ 2866
        // Net monthly: ~6099

        // Check mid-year (around 6 months = 180 days)
        DataPointPerDate midYear = findBalanceOnDate(res, testDate.plusDays(180));
        double expectedMidYear = 5000.0 + (6099.0 * 6); // Initial + 6 months of net
        assertTrue(midYear.dataPoint().balance() > expectedMidYear * 0.9,
                "Balance after 6 months should be approximately " + expectedMidYear);

        // Check end of year (around 365 days)
        DataPointPerDate endYear = findBalanceOnDate(res, testDate.plusDays(364));
        double expectedEndYear = 5000.0 + (6099.0 * 12); // Initial + 12 months of net
        assertTrue(endYear.dataPoint().balance() > expectedEndYear * 0.9,
                "Balance after 1 year should be approximately " + expectedEndYear);
        assertTrue(endYear.dataPoint().balance() < expectedEndYear * 1.1,
                "Balance shouldn't exceed expected by too much");

        // Verify we have entries for the full year
        assertTrue(res.size() >= 365, "Should have at least 365 days of projections for ONE_YEAR");
    }


}
