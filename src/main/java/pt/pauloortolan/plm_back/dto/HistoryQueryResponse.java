package pt.pauloortolan.plm_back.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record HistoryQueryResponse(
        String lender,
        String date,
        List<HistoryItem> history) {}