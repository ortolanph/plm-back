package pt.pauloortolan.plm_back.dto;

import pt.pauloortolan.plm_back.model.TransactionPaymentType;
import pt.pauloortolan.plm_back.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID lenderId,
        LocalDateTime transactionDate,
        BigDecimal transactionValue,
        TransactionType transactionType,
        TransactionPaymentType transactionPaymentType) {}