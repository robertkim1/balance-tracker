package com.pikel.balancetracker;

import java.time.LocalDate;
import java.util.List;

public class BalanceDataRequest {
    private List<Debt> debts;
    private List<Income> incomes;
    // assume start date is date of submission
    private ProjectionTimeframe projectionTimeframe;
    private SummarizeDateBy summarizeDateBy;

    public BalanceDataRequest() {
    }

    public List<Debt> getDebts() {
        return debts;
    }

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(List<Income> incomes) {
        this.incomes = incomes;
    }

    public ProjectionTimeframe getProjectionTimeframe() {
        return projectionTimeframe;
    }

    public void setProjectionTimeframe(ProjectionTimeframe projectionTimeframe) {
        this.projectionTimeframe = projectionTimeframe;
    }

    public SummarizeDateBy getSummarizeDateBy() {
        return summarizeDateBy;
    }

    public void setSummarizeDateBy(SummarizeDateBy summarizeDateBy) {
        this.summarizeDateBy = summarizeDateBy;
    }
}
