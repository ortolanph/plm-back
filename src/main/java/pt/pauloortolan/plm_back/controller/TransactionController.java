package pt.pauloortolan.plm_back.controller;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.service.*;

import java.math.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/lenders/{lenderId}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
        @PathVariable UUID lenderId,
        @RequestBody CreateTransactionRequest request) {
        log.info("TransactionController::create(lenderId={})", lenderId);
        return ResponseEntity.ok(transactionService.create(request));
    }

    @GetMapping
    public ResponseEntity<TransactionQueryResponse> query(
        @PathVariable UUID lenderId,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) BigDecimal minValue,
        @RequestParam(required = false) BigDecimal maxValue,
        @RequestParam(required = false) String type) {
        log.info("TransactionController::query(lenderId={})", lenderId);
        return ResponseEntity.ok(transactionService.query(lenderId, startDate, endDate, minValue, maxValue, type));
    }
}