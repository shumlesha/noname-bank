package com.bank.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @Check(name = "name_check", constraints = "name ~ '^[a-zA-Z\\s]*$'")
    private String name;

    @Column
    @Check(name = "description_check", constraints = "description ~ '^[a-zA-ZА-Яа-я\\s]*$'")
    private String description;

    @Column(nullable = false)
    private boolean banned = false;
}
