package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.DataPointPerDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/balance")
public class BalanceTrackerController {

    private static final Logger logger = LoggerFactory.getLogger(BalanceTrackerController.class);
    private final BalanceTrackerService balanceTrackerService;

    public BalanceTrackerController(BalanceTrackerService balanceTrackerService) {
        this.balanceTrackerService = balanceTrackerService;
    }

    @PostMapping("/submit")
    public ResponseEntity<List<DataPointPerDate>> submitBalanceData(@RequestBody BalanceDataRequest request) {
        logger.info("Received balance data request with {} transactions",
                request.transactions() != null ? request.transactions().size() : 0);

        List<DataPointPerDate> balanceSummary = balanceTrackerService.getBalanceSummary(request);

        logger.info("Successfully generated balance summary with {} data points", balanceSummary.size());
        return ResponseEntity.ok(balanceSummary);
    }
}