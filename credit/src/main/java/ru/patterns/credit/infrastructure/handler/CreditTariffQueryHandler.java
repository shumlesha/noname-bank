package ru.patterns.credit.infrastructure.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.query.GetAllCreditTariffsQuery;
import ru.patterns.credit.application.query.GetCreditTariffByIdQuery;
import ru.patterns.credit.domain.repository.CreditTariffRepository;
import ru.patterns.credit.shared.dto.CreditTariffDto;
import ru.patterns.credit.shared.mapper.CreditTariffMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditTariffQueryHandler {
    private final CreditTariffRepository creditTariffRepository;

    @Transactional(readOnly = true)
    public List<CreditTariffDto> handle(GetAllCreditTariffsQuery query) {
        return creditTariffRepository.findAll()
                .stream()
                .map(CreditTariffMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CreditTariffDto handle(GetCreditTariffByIdQuery query) {
        var tariff = creditTariffRepository.findById(query.tariffId())
                .orElseThrow(() -> new IllegalArgumentException("Тариф не найден"));
        return CreditTariffMapper.toDto(tariff);
    }
}
