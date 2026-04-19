package pt.pauloortolan.plm_back.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.pauloortolan.plm_back.model.GeneratedCode;
import pt.pauloortolan.plm_back.repository.GeneratedCodeRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {

    private final GeneratedCodeRepository repository;
    private final Random random = new Random();

    private static final int MIN_CODE = 100000;
    private static final int MAX_CODE = 999999;
    private static final int CODE_EXPIRE_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 100;

    @Transactional
    public GenerateResponse generateCode(String email) {
        log.info("CodeService::generateCode(email={})", email);
        String code;
        int attempts = 0;

        do {
            code = String.valueOf(random.nextInt(MAX_CODE - MIN_CODE + 1) + MIN_CODE);
            attempts++;
        } while (repository.findByCodeAndEmail(code, email).isPresent() && attempts < MAX_ATTEMPTS);

        if (attempts >= MAX_ATTEMPTS) {
            throw new IllegalStateException("Unable to generate unique code after " + MAX_ATTEMPTS + " attempts");
        }

        GeneratedCode entity = new GeneratedCode(code, email);
        repository.save(entity);

        return new GenerateResponse(code, email);
    }

    @Transactional(readOnly = true)
    public ValidateResponse validateCode(String code, String email) {
        log.info("CodeService::validateCode(code={}, email={})", code, email);
        return repository.findByCodeAndEmail(code, email)
                .map(entity -> {
                    LocalDateTime createdAt = entity.getCreatedAt();
                    LocalDateTime expiryTime = createdAt.plusMinutes(CODE_EXPIRE_MINUTES);
                    boolean expired = LocalDateTime.now().isAfter(expiryTime);

                    if (expired) {
                        return new ValidateResponse(false, "Code expired");
                    }
                    return new ValidateResponse(true, "Code valid");
                })
                .orElse(new ValidateResponse(false, "Code invalid"));
    }

    @Transactional
    @Scheduled(fixedRate = 60 * 1000)
    public void purgeExpiredCodes() {
        log.info("CodeService::purgeExpiredCodes()::at={}", LocalDateTime.now());
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(CODE_EXPIRE_MINUTES);
        int deleted = repository.deleteExpiredCodes(cutoff);
        if (deleted > 0) {
            log.info("Purged {} expired codes", deleted);
        }
    }

    public record GenerateResponse(String code, String email) {}

    public record ValidateResponse(boolean valid, String message) {}
}