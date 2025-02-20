package ru.patterns.credit.service.employee.credit.info;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.model.dto.CreditDto;
import ru.patterns.credit.repository.CreditRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeCreditServiceImpl implements EmployeeCreditService{
    private final CreditRepository creditRepository;

    @Override
    public List<CreditDto> getCreditsByCustomer(UUID customerId) {
        return creditRepository.findByCustomerId(customerId)
                .stream()
                .map(CreditDto::fromEntity)
                .toList();
    }

    @Override
    public CreditDto getCreditById(UUID creditId) {
        var credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));
        return CreditDto.fromEntity(credit);
    }
}
