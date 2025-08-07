package it.albergo.test.demo.dto;

import it.albergo.test.demo.enumeration.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String username;
    private Role role;
}
