package ru.patterns.credit.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "missed_payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MissedPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Credit credit;

    private LocalDate missedDate;

    private BigDecimal debtAmount;
}
