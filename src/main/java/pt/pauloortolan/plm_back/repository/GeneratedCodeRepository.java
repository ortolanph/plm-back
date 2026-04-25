package pt.pauloortolan.plm_back.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import pt.pauloortolan.plm_back.model.*;

import java.time.*;
import java.util.*;

public interface GeneratedCodeRepository extends JpaRepository<GeneratedCode, Long> {

    Optional<GeneratedCode> findByCodeAndEmail(String code, String email);

    @Modifying
    @Query("DELETE FROM GeneratedCode g WHERE g.createdAt < :cutoff")
    int deleteExpiredCodes(@Param("cutoff") LocalDateTime cutoff);
}