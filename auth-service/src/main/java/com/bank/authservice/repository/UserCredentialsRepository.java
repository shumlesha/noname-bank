package com.bank.authservice.repository;

import com.bank.authservice.entity.UserCredentials;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import static org.hibernate.jpa.HibernateHints.HINT_READ_ONLY;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    @QueryHints(@QueryHint(name = HINT_READ_ONLY, value = "true"))
    @Query("SELECT uc FROM UserCredentials uc WHERE LOWER(uc.email) = LOWER(:email)")
    Optional<UserCredentials> findByEmailReadOnly(String email);
}
