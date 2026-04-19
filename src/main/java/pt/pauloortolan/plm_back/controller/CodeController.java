package pt.pauloortolan.plm_back.controller;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.pauloortolan.plm_back.service.CodeService;

@Slf4j
@RestController
@RequestMapping("/codes")
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @PostMapping("/generate")
    public ResponseEntity<CodeService.GenerateResponse> generate(@RequestBody CodeRequest request) {
        log.info("CodeController::generate(email={})", request.email());
        CodeService.GenerateResponse response = codeService.generateCode(request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<CodeService.ValidateResponse> validate(@RequestBody CodeRequest request) {
        log.info("CodeController::validate(code={}, email={})", request.code(), request.email());
        CodeService.ValidateResponse response = codeService.validateCode(request.code(), request.email());
        if (response.valid()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    public record CodeRequest(String email, String code) {}
}