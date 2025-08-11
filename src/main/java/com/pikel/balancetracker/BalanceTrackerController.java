package com.pikel.balancetracker;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/balance")
public class BalanceTrackerController {

    private final BalanceTrackerService balanceTrackerService;

    public BalanceTrackerController(BalanceTrackerService balanceTrackerService) {
        this.balanceTrackerService = balanceTrackerService;
    }

    @PostMapping("/submit")
    public List<Double> submitBalanceData(@RequestBody BalanceDataRequest request) {
        return balanceTrackerService.getBalanceSummary(request);
    }
}
