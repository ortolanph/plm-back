package pt.pauloortolan.plm_back.dto;

import java.time.*;
import java.util.*;

public record TransactionQueryResponse(
    Integer total,
    String lender,
    LocalDateTime date,
    List<TransactionItem> transactions) {
}