package pt.pauloortolan.plm_back.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoryItem(
        String date,
        BigDecimal value,
        String type) {}