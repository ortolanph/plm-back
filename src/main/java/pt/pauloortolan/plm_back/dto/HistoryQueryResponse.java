package pt.pauloortolan.plm_back.dto;

import java.util.*;

public record HistoryQueryResponse(
    String lender,
    String date,
    List<HistoryItem> history) {
}