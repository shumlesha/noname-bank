package ru.patterns.credit.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.query.GetCreditByIdQuery;
import ru.patterns.credit.application.query.GetCreditsByClientQuery;
import ru.patterns.credit.shared.dto.CreditDto;
import ru.patterns.credit.infrastructure.handler.CreditQueryHandler;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credit/query")
public class CreditQueryController {
    private final CreditQueryHandler creditQueryHandler;

    @GetMapping("/{clientId}")
    public ResponseEntity<List<CreditDto>> getCreditsByClient(@PathVariable UUID clientId) {
        var query = new GetCreditsByClientQuery(clientId);
        var credits = creditQueryHandler.handle(query);
        return ResponseEntity.ok(credits);
    }

    @GetMapping("/credit/{creditId}")
    public ResponseEntity<CreditDto> getCreditById(@PathVariable UUID creditId) {
        var query = new GetCreditByIdQuery(creditId);
        var credit = creditQueryHandler.handle(query);
        return ResponseEntity.ok(credit);
    }
}
