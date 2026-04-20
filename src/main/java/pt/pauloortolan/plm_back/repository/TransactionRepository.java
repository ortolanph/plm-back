package pt.pauloortolan.plm_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.pauloortolan.plm_back.model.Transaction;
import pt.pauloortolan.plm_back.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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