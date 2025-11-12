package com.pikel.balancetracker.balance.model;

import java.time.LocalDate;

public record DataPointPerDate(
        DataPoint dataPoint,
        LocalDate date
) {}