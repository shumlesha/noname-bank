package ru.patterns.credit.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.query.GetMissedPaymentsByClientQuery;
import ru.patterns.credit.infrastructure.handler.query.MissedPaymentQueryHandler;
import ru.patterns.credit.shared.common.DefaultResponse;
import ru.patterns.credit.shared.dto.MissedPaymentDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credit/missed/query")
@Tag(name = "Чтение данных по пропущенным платежам")
public class MissedPaymentQueryController {
    private final MissedPaymentQueryHandler missedPaymentQueryHandler;

    @GetMapping("/{clientId}")
    @Operation(summary = "Получить просроченные платежи по кредиту")
    public ResponseEntity<DefaultResponse<List<MissedPaymentDto>>> getMissedPaymentsByClientId(@PathVariable UUID clientId) {
        var query = new GetMissedPaymentsByClientQuery(clientId);
        var response = missedPaymentQueryHandler.handle(query);
        return ResponseEntity.ok(DefaultResponse.success(response));
    }
}
