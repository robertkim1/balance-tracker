package com.pikel.balancetracker.balance;

import com.pikel.balancetracker.balance.model.BalanceDataRequest;
import com.pikel.balancetracker.balance.model.DataPointPerDate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test/balance")
@Profile("dev")
public class BalanceTrackerTestController {

    private final BalanceTrackerService balanceTrackerService;

    // Use a real UUID that exists in Supabase auth.users
    // Create this user manually in Supabase dashboard first
    private static final UUID TEST_USER_ID =
            UUID.fromString("f6af81c5-0c44-408c-809d-dd51f4f8f7a0");

    public BalanceTrackerTestController(BalanceTrackerService balanceTrackerService) {
        this.balanceTrackerService = balanceTrackerService;
    }

    @PostMapping("/submit")
    public ResponseEntity<List<DataPointPerDate>> testBalanceSubmit(
            @RequestBody BalanceDataRequest request) {

        // If your service needs userId to save transactions:
        // balanceTrackerService.getBalanceSummary(request, TEST_USER_ID);

        List<DataPointPerDate> balanceSummary =
                balanceTrackerService.getBalanceSummary(request);

        return ResponseEntity.ok(balanceSummary);
    }
}