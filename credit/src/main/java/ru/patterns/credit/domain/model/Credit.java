package ru.patterns.credit.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "credits")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID clientId;

    private UUID accountId;

    private BigDecimal amount;

    private BigDecimal paidAmount = BigDecimal.ZERO;

    @ManyToOne
    private CreditTariff tariff;

    @Enumerated(EnumType.STRING)
    private CreditStatus status;

    public void addPayment(BigDecimal payment) {
        this.paidAmount = this.paidAmount.add(payment);
    }

    public boolean isPaidOff() {
        return this.paidAmount.compareTo(this.amount) >= 0;
    }
}
