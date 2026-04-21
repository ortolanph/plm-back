package pt.pauloortolan.plm_back.dto;

import pt.pauloortolan.plm_back.model.TransactionPaymentType;
import pt.pauloortolan.plm_back.model.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(
        UUID idLender,
        BigDecimal transactionValue,
        TransactionType transactionType,
        TransactionPaymentType transactionPaymentType,
        UUID cancelTransactionId) {}