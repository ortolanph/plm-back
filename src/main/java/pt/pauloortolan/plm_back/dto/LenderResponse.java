package pt.pauloortolan.plm_back.dto;

import java.time.*;
import java.util.*;

public record LenderResponse(
    UUID id,
    String name,
    String phone,
    String bankData,
    String address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
}