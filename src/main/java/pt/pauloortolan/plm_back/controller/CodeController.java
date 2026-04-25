package pt.pauloortolan.plm_back.controller;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.service.*;

@Slf4j
@RestController
@RequestMapping("/codes")
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @PostMapping("/generate")
    public ResponseEntity<GenerateResponse> generate(@RequestBody GenerateRequest request) {
        log.info("CodeController::generate(email={})", request.email());
        GenerateResponse response = codeService.generateCode(request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@RequestBody ValidateRequest request) {
        log.info("CodeController::validate(code={}, email={})", request.code(), request.email());
        ValidateResponse response = codeService.validateCode(request.code(), request.email());
        if (response.valid()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}