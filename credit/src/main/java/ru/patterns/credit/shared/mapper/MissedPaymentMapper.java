package ru.patterns.credit.shared.mapper;

import org.springframework.stereotype.Component;
import ru.patterns.credit.domain.model.MissedPayment;
import ru.patterns.credit.shared.dto.MissedPaymentDto;

import java.util.List;

@Component
public class MissedPaymentMapper {
    public MissedPaymentDto toDto(MissedPayment missedPayment) {
        return new MissedPaymentDto(
                missedPayment.getId(),
                missedPayment.getCredit().getId(),
                missedPayment.getCredit().getClientId(),
                missedPayment.getMissedDate(),
                missedPayment.getDebt(),
                missedPayment.getAmount()
        );
    }

    public List<MissedPaymentDto> toDtoList(List<MissedPayment> missedPayments) {
        return missedPayments.stream()
                .map(this::toDto)
                .toList();
    }
}
