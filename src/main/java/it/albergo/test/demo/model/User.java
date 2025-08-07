package it.albergo.test.demo.model;

import it.albergo.test.demo.enumeration.Role;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String email;
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    private Role role;
}
