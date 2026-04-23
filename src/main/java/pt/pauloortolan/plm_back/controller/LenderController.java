package pt.pauloortolan.plm_back.controller;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.service.LenderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/lenders")
@RequiredArgsConstructor
public class LenderController {

    private final LenderService lenderService;

    @PostMapping
    public ResponseEntity<LenderResponse> create(@RequestBody CreateLenderRequest request) {
        log.info("LenderController::create(name={})", request.name());
        return ResponseEntity.ok(lenderService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LenderResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateLenderRequest request) {
        log.info("LenderController::update(id={})", id);
        return ResponseEntity.ok(lenderService.update(id, request));
    }

    @GetMapping
    public ResponseEntity<List<LenderResponse>> query(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {
        log.info("LenderController::query(name={}, phone={})", name, phone);
        return ResponseEntity.ok(lenderService.query(name, phone));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LenderResponse> getById(@PathVariable UUID id) {
        log.info("LenderController::getById(id={})", id);
        return ResponseEntity.ok(lenderService.getById(id));
    }

    @PostMapping("/settle")
    public ResponseEntity<Void> settle(@RequestBody SettleLenderRequest request) {
        log.info("LenderController::settle(lenderId={}, settlementType={})", 
                request.lenderId(), request.settlementType());
        lenderService.settleLender(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("LenderController::delete(id={})", id);
        lenderService.deleteLender(id);
        return ResponseEntity.noContent().build();
    }
}