package ru.patterns.credit.presentation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.query.GetCreditRatingByClientQuery;
import ru.patterns.credit.infrastructure.handler.query.CreditRatingQueryHandler;
import ru.patterns.credit.shared.common.DefaultResponse;
import ru.patterns.credit.shared.response.credit.rating.CreditRatingResponse;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credit/rating/query")
@Tag(name = "Чтение данных по кредитному рейтингу клиента")
public class CreditRatingQueryController {
    private final CreditRatingQueryHandler creditRatingQueryHandler;

    @GetMapping("/{clientId}")
    public ResponseEntity<DefaultResponse<CreditRatingResponse>> getCreditRatingByClientId(@PathVariable UUID clientId) {
        var query = new GetCreditRatingByClientQuery(clientId);
        return ResponseEntity.ok(DefaultResponse.success(creditRatingQueryHandler.handle(query)));
    }
}
