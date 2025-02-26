package com.bank.userservice.repository;

import com.bank.userservice.entity.User;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import static org.hibernate.jpa.HibernateHints.HINT_READ_ONLY;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    @QueryHints(@QueryHint(name = HINT_READ_ONLY, value = "true"))
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailReadOnly(String email);
}