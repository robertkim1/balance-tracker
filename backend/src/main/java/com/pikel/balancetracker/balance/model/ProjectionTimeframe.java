package com.pikel.balancetracker.balance.model;

import lombok.Getter;

@Getter
public enum ProjectionTimeframe {
    ONE_YEAR(1),
    TWO_YEARS(2),
    FIVE_YEARS(5);

    private final int years;

    ProjectionTimeframe(int years) {
        this.years = years;
    }

}
