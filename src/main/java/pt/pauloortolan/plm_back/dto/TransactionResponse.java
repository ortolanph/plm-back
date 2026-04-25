package pt.pauloortolan.plm_back.dto;

import pt.pauloortolan.plm_back.model.*;

import java.math.*;
import java.time.*;
import java.util.*;

public record TransactionResponse(
    UUID id,
    UUID lenderId,
    LocalDateTime transactionDate,
    BigDecimal transactionValue,
    TransactionType transactionType,
    TransactionPaymentType transactionPaymentType) {
}