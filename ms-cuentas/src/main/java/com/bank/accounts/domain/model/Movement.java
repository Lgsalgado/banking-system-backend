package com.bank.accounts.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movementId;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String movementType;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private Long accountId;
}
