package ru.patterns.credit.infrastructure.handler.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.query.GetCreditByIdQuery;
import ru.patterns.credit.application.query.GetCreditsByClientQuery;
import ru.patterns.credit.shared.dto.CreditDto;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.shared.mapper.CreditMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditQueryHandler {
    private final CreditRepository creditRepository;

    @Transactional(readOnly = true)
    public List<CreditDto> handle(GetCreditsByClientQuery query) {
        return creditRepository.findByClientId(query.clientId())
                .stream()
                .map(CreditMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CreditDto handle(GetCreditByIdQuery query) {
        var credit = creditRepository.findById(query.creditId())
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));
        return CreditMapper.toDto(credit);
    }
}
