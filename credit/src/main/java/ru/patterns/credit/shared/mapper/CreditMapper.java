package ru.patterns.credit.shared.mapper;

import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.shared.dto.CreditDto;

public class CreditMapper {
    public static CreditDto toDto(Credit credit) {
        return new CreditDto(
                credit.getId(),
                credit.getClientId(),
                credit.getAccountId(),
                credit.getAmount(),
                credit.getPaidAmount(),
                credit.getNextPaymentDate(),
                credit.getTariff() != null ? credit.getTariff().getName() : null,
                credit.getTariff() != null ? credit.getTariff().getInterestRate() : null,
                credit.getStatus()
        );
    }
}
