package pt.pauloortolan.plm_back.dto;

import java.math.*;
import java.util.*;

public record LenderSummaryResponse(
    BigDecimal total,
    String lender,
    String date,
    List<TransactionItem> transactions,
    List<HistoryItem> history) {
}