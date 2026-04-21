package pt.pauloortolan.plm_back.dto;

import pt.pauloortolan.plm_back.model.HistoryType;
import pt.pauloortolan.plm_back.model.TransactionPaymentType;

import java.util.UUID;

public record SettleLenderRequest(
        UUID lenderId,
        HistoryType settlementType,
        TransactionPaymentType paymentType
) {}