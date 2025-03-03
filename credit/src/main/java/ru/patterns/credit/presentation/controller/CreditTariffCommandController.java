package ru.patterns.credit.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.command.CreateCreditTariffCommand;
import ru.patterns.credit.application.command.DeleteCreditTariffCommand;
import ru.patterns.credit.application.command.UpdateCreditTariffCommand;
import ru.patterns.credit.infrastructure.handler.command.CreditTariffCommandHandler;
import ru.patterns.credit.shared.common.DefaultResponse;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credit/tariff/command")
@Tag(name = "Команды кредитных тарифов")
public class CreditTariffCommandController {
    private final CreditTariffCommandHandler creditTariffCommandHandler;

    @PostMapping("/create")
    @Operation(summary = "Создать кредитный тариф")
    public ResponseEntity<DefaultResponse<UUID>> createTariff(@RequestBody CreateCreditTariffCommand command) {
        var tariffId = creditTariffCommandHandler.handle(command);
        return ResponseEntity.ok(DefaultResponse.success(tariffId));
    }

    @PutMapping("/update")
    @Operation(summary = "Редактировать кредитный тариф")
    public ResponseEntity<DefaultResponse<Void>> updateTariff(@RequestBody UpdateCreditTariffCommand command) {
        creditTariffCommandHandler.handle(command);
        return ResponseEntity.ok(DefaultResponse.success(null));
    }

    @DeleteMapping("/delete/{tariffId}")
    @Operation(summary = "Удалить кредитный тариф")
    public ResponseEntity<DefaultResponse<Void>> deleteTariff(@PathVariable UUID tariffId) {
        var command = new DeleteCreditTariffCommand(tariffId);
        creditTariffCommandHandler.handle(command);
        return ResponseEntity.ok(DefaultResponse.success(null));
    }
}


