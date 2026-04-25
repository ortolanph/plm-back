package pt.pauloortolan.plm_back.repository;

import org.jspecify.annotations.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import pt.pauloortolan.plm_back.model.*;

import java.util.*;

public interface LenderRepository extends JpaRepository<Lender, UUID> {

    @NullMarked
    Optional<Lender> findById(UUID id);

    @Query("SELECT l FROM Lender l WHERE l.name LIKE %:name%")
    java.util.List<Lender> findByNameContaining(@Param("name") String name);

    @Query("SELECT l FROM Lender l WHERE l.phone LIKE %:phone%")
    java.util.List<Lender> findByPhoneContaining(@Param("phone") String phone);

    @Query("SELECT l FROM Lender l WHERE " +
        "(:name IS NULL OR l.name LIKE %:name%) AND " +
        "(:phone IS NULL OR l.phone LIKE %:phone%)")
    java.util.List<Lender> findByFilters(
        @Param("name") String name,
        @Param("phone") String phone);
}