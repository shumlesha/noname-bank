package ru.patterns.credit.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.query.GetCreditByIdQuery;
import ru.patterns.credit.application.query.GetCreditsByClientQuery;
import ru.patterns.credit.infrastructure.handler.query.CreditQueryHandler;
import ru.patterns.credit.shared.common.DefaultResponse;
import ru.patterns.credit.shared.dto.CreditDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credit/query")
@Tag(name = "Чтение данных по кредиту")
public class CreditQueryController {
    private final CreditQueryHandler creditQueryHandler;

    @GetMapping("/{clientId}")
    @Operation(summary = "Получение списка кредитов по ID клиента")
    public ResponseEntity<DefaultResponse<List<CreditDto>>> getCreditsByClient(@PathVariable UUID clientId) {
        var query = new GetCreditsByClientQuery(clientId);
        var credits = creditQueryHandler.handle(query);
        return ResponseEntity.ok(DefaultResponse.success(credits));
    }

    @GetMapping("/credit/{creditId}")
    @Operation(summary = "Получение данных кредита по ID")
    public ResponseEntity<DefaultResponse<CreditDto>> getCreditById(@PathVariable UUID creditId) {
        var query = new GetCreditByIdQuery(creditId);
        var credit = creditQueryHandler.handle(query);
        return ResponseEntity.ok(DefaultResponse.success(credit));
    }
}

