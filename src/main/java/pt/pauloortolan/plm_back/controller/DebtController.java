package pt.pauloortolan.plm_back.controller;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.service.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DebtController {

    private final LenderService lenderService;

    @GetMapping("/debts")
    public ResponseEntity<DebtsResponse> getDebts() {
        log.info("DebtController::getDebts()");
        return ResponseEntity.ok(lenderService.getDebts());
    }
}