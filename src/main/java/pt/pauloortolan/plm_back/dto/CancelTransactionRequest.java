package pt.pauloortolan.plm_back.dto;

import java.util.UUID;

public record CancelTransactionRequest(UUID transactionId) {}