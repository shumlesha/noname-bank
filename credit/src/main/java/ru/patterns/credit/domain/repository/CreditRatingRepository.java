package ru.patterns.credit.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.credit.domain.model.CreditRating;

import java.util.Optional;
import java.util.UUID;

public interface CreditRatingRepository extends JpaRepository<CreditRating, UUID> {
    Optional<CreditRating> findByClientId(UUID clientId);
}
