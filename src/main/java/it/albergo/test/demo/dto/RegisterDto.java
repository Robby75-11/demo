package it.albergo.test.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterDto {

    @NotEmpty(message = "Il nome utente è obbligatorio")
    private String username;

    @NotEmpty(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    @NotEmpty(message = "La password è obbligatoria")
    private String password;
}

