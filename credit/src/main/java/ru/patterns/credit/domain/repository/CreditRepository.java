package ru.patterns.credit.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.patterns.credit.domain.model.Credit;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CreditRepository extends JpaRepository<Credit, UUID> {
    List<Credit> findByClientId(UUID clientId);

    @Query("SELECT c FROM Credit c WHERE c.nextPaymentDate <= :currentDate AND c.status = 'ACTIVE'")
    List<Credit> findCreditsDueForPayment(@Param("currentDate") LocalDate currentDate);
}
