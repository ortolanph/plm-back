package pt.pauloortolan.plm_back.dto;

import java.math.*;

public record HistoryItem(
    String date,
    BigDecimal value,
    String type) {
}