package com.example.toke.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRegistroDTO {

    @NotEmpty(message = "El nombre no puede estar vacío.")
    private String nombre;

    @NotEmpty(message = "El apellido no puede estar vacío.")
    private String apellido;

    @NotEmpty(message = "El email no puede estar vacío.")
    @Email(message = "Debe ser una dirección de email válida.")
    private String email;

    @NotEmpty(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;
}