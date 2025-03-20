package ru.patterns.credit.infrastructure.handler.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.query.GetMissedPaymentsByClientQuery;
import ru.patterns.credit.infrastructure.service.MissedPaymentService;
import ru.patterns.credit.shared.dto.MissedPaymentDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissedPaymentQueryHandler {
    private final MissedPaymentService missedPaymentService;

    public List<MissedPaymentDto> handle(GetMissedPaymentsByClientQuery query) {
        return missedPaymentService.getMissedPaymentsByClientId(query.clientId());
    }
}
