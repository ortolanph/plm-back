package pt.pauloortolan.plm_back.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lenders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lender {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "lender_name", nullable = false)
    private String name;

    @Column(name = "lender_phone")
    private String phone;

    @Column(name = "lender_bank_data")
    private String bankData;

    @Column(name = "lender_address")
    private String address;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}