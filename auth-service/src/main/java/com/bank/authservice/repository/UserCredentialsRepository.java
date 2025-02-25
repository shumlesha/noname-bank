package com.bank.authservice.repository;

import com.bank.authservice.entity.UserCredentials;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import static org.hibernate.jpa.HibernateHints.HINT_READ_ONLY;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {

    @QueryHints(@QueryHint(name = HINT_READ_ONLY, value = "true"))
    @Query("SELECT u FROM UserCredentials u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(String email);
}
