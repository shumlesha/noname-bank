package ru.patterns.credit.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.model.CreditStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditDto {
    private UUID id;
    private UUID clientId;
    private UUID accountId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate nextPaymentDate;
    private String tariffName;
    private BigDecimal interestRate;
    private CreditStatus status;
}

