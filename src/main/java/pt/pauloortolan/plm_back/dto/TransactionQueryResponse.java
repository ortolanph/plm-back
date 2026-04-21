package pt.pauloortolan.plm_back.dto;

import pt.pauloortolan.plm_back.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TransactionQueryResponse(
        Integer total,
        String lender,
        LocalDateTime date,
        List<TransactionItem> transactions) {}