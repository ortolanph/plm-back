package pt.pauloortolan.plm_back.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "generated_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 6)
    private String code;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "email", nullable = false)
    private String email;

    public GeneratedCode(String code, String email) {
        this.code = code;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
}