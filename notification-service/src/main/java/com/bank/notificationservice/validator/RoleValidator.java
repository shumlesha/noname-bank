package com.bank.notificationservice.validator;

import com.bank.notificationservice.enumeration.Role;
import lombok.experimental.UtilityClass;
import java.util.List;

@UtilityClass
public class RoleValidator {
    public boolean isReceivedRoleMatchesUserRole(List<String> userRoles, Role receivedRole) {
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }

        return userRoles.stream()
                .filter(role -> List.of("EMPLOYEE", "CLIENT").contains(role))
                .map(Role::valueOf)
                .anyMatch(role -> role.equals(receivedRole));
    }
}
