package pt.pauloortolan.plm_back.dto;

import java.math.BigDecimal;
import java.util.List;

public record DebtsResponse(
    BigDecimal totalDebt,
    String date,
    List<DebtDetail> details) {

    public record DebtDetail(
        String lender,
        BigDecimal total) {
    }
}