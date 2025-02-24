package ru.patterns.credit.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.query.GetAllCreditTariffsQuery;
import ru.patterns.credit.application.query.GetCreditTariffByIdQuery;
import ru.patterns.credit.infrastructure.handler.query.CreditTariffQueryHandler;
import ru.patterns.credit.shared.dto.CreditTariffDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credit-tariff/query")
public class CreditTariffQueryController {
    private final CreditTariffQueryHandler creditTariffQueryHandler;

    @GetMapping("/all")
    public ResponseEntity<List<CreditTariffDto>> getAllTariffs() {
        var query = new GetAllCreditTariffsQuery();
        var tariffs = creditTariffQueryHandler.handle(query);
        return ResponseEntity.ok(tariffs);
    }

    @GetMapping("/{tariffId}")
    public ResponseEntity<CreditTariffDto> getTariffById(@PathVariable UUID tariffId) {
        var query = new GetCreditTariffByIdQuery(tariffId);
        var tariff = creditTariffQueryHandler.handle(query);
        return ResponseEntity.ok(tariff);
    }
}
