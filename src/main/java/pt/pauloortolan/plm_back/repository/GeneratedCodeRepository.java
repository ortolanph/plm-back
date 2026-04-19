package pt.pauloortolan.plm_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.pauloortolan.plm_back.model.GeneratedCode;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GeneratedCodeRepository extends JpaRepository<GeneratedCode, Long> {

    Optional<GeneratedCode> findByCodeAndEmail(String code, String email);

    @Modifying
    @Query("DELETE FROM GeneratedCode g WHERE g.createdAt < :cutoff")
    int deleteExpiredCodes(@Param("cutoff") LocalDateTime cutoff);
}