package pt.pauloortolan.plm_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.pauloortolan.plm_back.model.TransactionHistory;

import java.util.UUID;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {
}