package pt.pauloortolan.plm_back.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false)
    private UUID id;

    @Column(name = "history_date", nullable = false)
    private LocalDateTime historyDate;

    @Column(name = "lender_name", nullable = false)
    private String lenderName;

    @Column(name = "lender_phone")
    private String lenderPhone;

    @Column(name = "lender_bank_data")
    private String lenderBankData;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "transaction_value")
    private BigDecimal transactionValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_payment_type")
    private TransactionPaymentType transactionPaymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "history_type", nullable = false)
    private HistoryType historyType;

    @PrePersist
    void onCreate() {
        if (this.historyDate == null) {
            this.historyDate = LocalDateTime.now();
        }
    }
}