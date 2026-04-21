package pt.pauloortolan.plm_back.dto;

import pt.pauloortolan.plm_back.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionItem(
        LocalDateTime date,
        BigDecimal value,
        TransactionType type) {}