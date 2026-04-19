package pt.pauloortolan.plm_back.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.pauloortolan.plm_back.model.Lender;
import pt.pauloortolan.plm_back.repository.LenderRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LenderService {

    private final LenderRepository repository;

    @Transactional
    public LenderResponse create(CreateLenderRequest request) {
        log.info("LenderService::create(name={})", request.name());
        Lender lender = Lender.builder()
                .name(request.name())
                .phone(request.phone())
                .bankData(request.bankData())
                .address(request.address())
                .build();
        lender = repository.save(lender);
        return toResponse(lender);
    }

    @Transactional
    public LenderResponse update(UUID id, UpdateLenderRequest request) {
        log.info("LenderService::update(id={})", id);
        Lender lender = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + id));
        
        if (request.name() != null) lender.setName(request.name());
        if (request.phone() != null) lender.setPhone(request.phone());
        if (request.bankData() != null) lender.setBankData(request.bankData());
        if (request.address() != null) lender.setAddress(request.address());
        
        lender = repository.save(lender);
        return toResponse(lender);
    }

    @Transactional(readOnly = true)
    public List<LenderResponse> query(String name, String phone) {
        log.info("LenderService::query(name={}, phone={})", name, phone);
        
        if (name != null && phone != null) {
            return repository.findByFilters(name, phone).stream()
                    .map(this::toResponse)
                    .toList();
        }
        if (name != null) {
            return repository.findByNameContaining(name).stream()
                    .map(this::toResponse)
                    .toList();
        }
        if (phone != null) {
            return repository.findByPhoneContaining(phone).stream()
                    .map(this::toResponse)
                    .toList();
        }
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LenderResponse getById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + id));
    }

    private LenderResponse toResponse(Lender lender) {
        return new LenderResponse(
                lender.getId(),
                lender.getName(),
                lender.getPhone(),
                lender.getBankData(),
                lender.getAddress(),
                lender.getCreatedAt(),
                lender.getUpdatedAt()
        );
    }

    public record CreateLenderRequest(String name, String phone, String bankData, String address) {}

    public record UpdateLenderRequest(String name, String phone, String bankData, String address) {}

    public record LenderResponse(
            UUID id,
            String name,
            String phone,
            String bankData,
            String address,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime updatedAt) {}
}