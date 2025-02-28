package ru.patterns.credit.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.patterns.credit.domain.model.CreditTariff;

import java.util.UUID;

@Repository
public interface CreditTariffRepository extends JpaRepository<CreditTariff, UUID> {
}
