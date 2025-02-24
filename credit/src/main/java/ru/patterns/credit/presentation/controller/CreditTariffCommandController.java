package ru.patterns.credit.presentation.controller;

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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credit-tariff/command")
public class CreditTariffCommandController {
    private final CreditTariffCommandHandler creditTariffCommandHandler;

    @PostMapping("/create")
    public ResponseEntity<UUID> createTariff(@RequestBody CreateCreditTariffCommand command) {
        UUID tariffId = creditTariffCommandHandler.handle(command);
        return ResponseEntity.ok(tariffId);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateTariff(@RequestBody UpdateCreditTariffCommand command) {
        creditTariffCommandHandler.handle(command);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{tariffId}")
    public ResponseEntity<Void> deleteTariff(@PathVariable UUID tariffId) {
        var command = new DeleteCreditTariffCommand(tariffId);
        creditTariffCommandHandler.handle(command);
        return ResponseEntity.ok().build();
    }
}

