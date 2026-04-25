package pt.pauloortolan.plm_back.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import pt.pauloortolan.plm_back.model.*;

import java.math.*;
import java.time.*;
import java.util.*;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByLenderId(UUID lenderId);

    @Query("SELECT t FROM Transaction t WHERE t.lender.id = :lenderId AND " +
        "(:startDate IS NULL OR t.transactionDate >= :startDate) AND " +
        "(:endDate IS NULL OR t.transactionDate <= :endDate) AND " +
        "(:minValue IS NULL OR t.transactionValue >= :minValue) AND " +
        "(:maxValue IS NULL OR t.transactionValue <= :maxValue) AND " +
        "(:type IS NULL OR t.transactionType = :type)")
    List<Transaction> findByFilters(
        @Param("lenderId") UUID lenderId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("minValue") BigDecimal minValue,
        @Param("maxValue") BigDecimal maxValue,
        @Param("type") TransactionType type);
}