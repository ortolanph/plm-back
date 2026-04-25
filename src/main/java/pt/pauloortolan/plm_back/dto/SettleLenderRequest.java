package pt.pauloortolan.plm_back.dto;

import pt.pauloortolan.plm_back.model.*;

import java.util.*;

public record SettleLenderRequest(
    UUID lenderId,
    HistoryType settlementType,
    TransactionPaymentType paymentType
) {
}