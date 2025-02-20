package ru.patterns.credit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.model.dto.CreditDto;
import ru.patterns.credit.service.employee.credit.info.EmployeeCreditService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee/credit/info")
public class EmployeeCreditInformationController {
    private final EmployeeCreditService employeeCreditService;

    @GetMapping("/{customerId}")
    public ResponseEntity<List<CreditDto>> getCreditsByCustomer(@PathVariable UUID customerId) {
        var credits = employeeCreditService.getCreditsByCustomer(customerId);
        return ResponseEntity.ok(credits);
    }

    @GetMapping("/{creditId}")
    public ResponseEntity<CreditDto> getCreditById(@PathVariable UUID creditId) {
        CreditDto credit = employeeCreditService.getCreditById(creditId);
        return ResponseEntity.ok(credit);
    }
}
