package pt.pauloortolan.plm_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.pauloortolan.plm_back.model.TransactionHistory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {

    @Query("SELECT th FROM TransactionHistory th WHERE th.lenderId = :lenderId " +
            "AND (:startDate IS NULL OR th.historyDate >= :startDate) " +
            "AND (:endDate IS NULL OR th.historyDate <= :endDate) " +
            "AND (:minValue IS NULL OR th.transactionValue >= :minValue) " +
            "AND (:maxValue IS NULL OR th.transactionValue <= :maxValue) " +
            "AND (:type IS NULL OR th.historyType = :type) " +
            "ORDER BY th.historyDate DESC")
    List<TransactionHistory> findByFilters(
            @Param("lenderId") UUID lenderId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minValue") BigDecimal minValue,
            @Param("maxValue") BigDecimal maxValue,
            @Param("type") String type);
}