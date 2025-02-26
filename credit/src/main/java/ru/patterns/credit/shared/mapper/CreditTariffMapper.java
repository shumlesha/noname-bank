package ru.patterns.credit.shared.mapper;

import ru.patterns.credit.domain.model.CreditTariff;
import ru.patterns.credit.shared.dto.CreditTariffDto;

public class CreditTariffMapper {
    public static CreditTariffDto toDto(CreditTariff creditTariff) {
        return new CreditTariffDto(
                creditTariff.getId(),
                creditTariff.getName(),
                creditTariff.getInterestRate()
        );
    }

    public static CreditTariff toEntity(CreditTariffDto dto) {
        return new CreditTariff(
                dto.getId(),
                dto.getName(),
                dto.getInterestRate()
        );
    }
}

