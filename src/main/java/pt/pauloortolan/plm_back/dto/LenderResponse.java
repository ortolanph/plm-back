package pt.pauloortolan.plm_back.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LenderResponse(
        UUID id,
        String name,
        String phone,
        String bankData,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}