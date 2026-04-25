package pt.pauloortolan.plm_back.dto;

import pt.pauloortolan.plm_back.model.*;

import java.math.*;
import java.time.*;

public record TransactionItem(
    LocalDateTime date,
    BigDecimal value,
    TransactionType type) {
}