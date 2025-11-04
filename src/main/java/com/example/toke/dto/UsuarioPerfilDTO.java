package com.example.toke.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UsuarioPerfilDTO {
    
    @NotEmpty(message = "El nombre no puede estar vacío.")
    private String nombre;

    @NotEmpty(message = "El apellido no puede estar vacío.")
    private String apellido;

    @NotEmpty
    @Email
    private String email; // No será editable, solo para mostrar

    private String direccion;
    private String telefono;
}