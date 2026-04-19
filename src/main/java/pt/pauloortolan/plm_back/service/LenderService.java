package pt.pauloortolan.plm_back.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.pauloortolan.plm_back.dto.CreateLenderRequest;
import pt.pauloortolan.plm_back.dto.LenderResponse;
import pt.pauloortolan.plm_back.dto.UpdateLenderRequest;
import pt.pauloortolan.plm_back.mapper.LenderMapper;
import pt.pauloortolan.plm_back.model.Lender;
import pt.pauloortolan.plm_back.repository.LenderRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LenderService {

    private final LenderRepository repository;
    private final LenderMapper mapper;

    @Transactional
    public LenderResponse create(CreateLenderRequest request) {
        log.info("LenderService::create(name={})", request.name());
        Lender lender = mapper.toEntity(request);
        lender = repository.save(lender);
        return mapper.toResponse(lender);
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
        return mapper.toResponse(lender);
    }

    @Transactional(readOnly = true)
    public List<LenderResponse> query(String name, String phone) {
        log.info("LenderService::query(name={}, phone={})", name, phone);
        
        if (name != null && phone != null) {
            return repository.findByFilters(name, phone).stream()
                    .map(mapper::toResponse)
                    .toList();
        }
        if (name != null) {
            return repository.findByNameContaining(name).stream()
                    .map(mapper::toResponse)
                    .toList();
        }
        if (phone != null) {
            return repository.findByPhoneContaining(phone).stream()
                    .map(mapper::toResponse)
                    .toList();
        }
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LenderResponse getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Lender not found: " + id));
    }
}