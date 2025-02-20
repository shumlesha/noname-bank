package ru.patterns.credit.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.patterns.credit.entity.Credit;
import ru.patterns.credit.model.enums.CreditStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditDto {

    private UUID id;
    private UUID clientId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private String tariffName;
    private BigDecimal interestRate;
    private CreditStatus status;

    public static CreditDto fromEntity(Credit credit) {
        return new CreditDto(
                credit.getId(),
                credit.getClientId(),
                credit.getAmount(),
                credit.getPaidAmount(),
                credit.getTariff() != null ? credit.getTariff().getName() : null,
                credit.getTariff() != null ? credit.getTariff().getInterestRate() : null,
                credit.getStatus()
        );
    }
}

