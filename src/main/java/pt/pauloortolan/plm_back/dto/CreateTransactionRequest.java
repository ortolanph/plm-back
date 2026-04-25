package pt.pauloortolan.plm_back.dto;

import pt.pauloortolan.plm_back.model.*;

import java.math.*;
import java.util.*;

public record CreateTransactionRequest(
    UUID idLender,
    BigDecimal transactionValue,
    TransactionType transactionType,
    TransactionPaymentType transactionPaymentType,
    UUID cancelTransactionId) {
}