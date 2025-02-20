package ru.patterns.credit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.patterns.credit.entity.Credit;

import java.util.List;
import java.util.UUID;

@Repository
public interface CreditRepository extends JpaRepository<Credit, UUID> {

    List<Credit> findByCustomerId(UUID customerId);

}
