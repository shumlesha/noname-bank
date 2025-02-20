package ru.patterns.credit.service.employee.credit.info;

import ru.patterns.credit.model.dto.CreditDto;

import java.util.List;
import java.util.UUID;

public interface EmployeeCreditService {
    List<CreditDto> getCreditsByCustomer(UUID customerId);
    CreditDto getCreditById(UUID creditId);
}
