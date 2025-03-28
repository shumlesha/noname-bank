package ru.patterns.interfacecontrol.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.patterns.interfacecontrol.enums.Theme;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSettings {
    @Id
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    @ElementCollection
    @CollectionTable(name = "hidden_accounts", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "account_id")
    private Set<String> hiddenAccounts;
}
