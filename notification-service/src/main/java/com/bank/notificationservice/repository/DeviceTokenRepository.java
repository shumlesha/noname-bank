package com.bank.notificationservice.repository;

import com.bank.notificationservice.entity.DeviceToken;
import com.bank.notificationservice.enumeration.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {
    Optional<DeviceToken> findByToken(String token);

    void deleteByToken(String token);

    @Query(nativeQuery = true, value = """
            SELECT dt.token
            FROM device_tokens dt
            WHERE dt.user_id = :userId
            AND dt.user_role_on_device IN (:roles)
            """)
    List<String> findAllTokensByUserIdAndRoles(UUID userId, List<String> roles);


    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM device_tokens dt
            WHERE dt.token IN (:tokens)
            """)
    int deleteByTokens(List<String> tokens);
}
