package ru.patterns.credit.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.command.CreateCreditCommand;
import ru.patterns.credit.application.command.PayCreditCommand;
import ru.patterns.credit.infrastructure.handler.command.CreditCommandHandler;

import java.util.UUID;

@RestController
@RequestMapping("/api/credit/command")
@RequiredArgsConstructor
public class CreditCommandController {
    private final CreditCommandHandler creditCommandHandler;

    @PostMapping
    public ResponseEntity<UUID> createCredit(@RequestBody CreateCreditCommand command) {
        var creditId = creditCommandHandler.handle(command);
        return ResponseEntity.ok(creditId);
    }

    @PostMapping("/pay")
    public ResponseEntity<Void> payCredit(@RequestBody PayCreditCommand command) {
        creditCommandHandler.handle(command);
        return ResponseEntity.ok().build();
    }
}
