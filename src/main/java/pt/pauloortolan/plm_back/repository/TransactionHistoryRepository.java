package pt.pauloortolan.plm_back.repository;

import org.springframework.data.jpa.repository.*;
import pt.pauloortolan.plm_back.model.*;

import java.math.*;
import java.time.*;
import java.util.*;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID>, JpaSpecificationExecutor<TransactionHistory> {

    List<TransactionHistory> findByLenderId(UUID lenderId);

    void deleteByHistoryDateBefore(LocalDateTime cutoffDate);

    default List<TransactionHistory> findByFilters(UUID lenderId, LocalDateTime startDate, LocalDateTime endDate,
                                                   BigDecimal minValue, BigDecimal maxValue, HistoryType type) {
        return findAll((root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (lenderId != null) {
                predicates.add(cb.equal(root.get("lenderId"), lenderId));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("historyDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("historyDate"), endDate));
            }
            if (minValue != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionValue"), minValue));
            }
            if (maxValue != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionValue"), maxValue));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("historyType"), type));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        });
    }
}