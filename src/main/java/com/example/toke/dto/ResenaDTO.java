package com.example.toke.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResenaDTO {
    private Long id;

    @NotNull(message = "La calificación es obligatoria.")
    @Min(value = 1, message = "La calificación mínima es 1.")
    @Max(value = 5, message = "La calificación máxima es 5.")
    private int calificacion;

    @NotEmpty(message = "El comentario no puede estar vacío.")
    private String comentario;

    private LocalDateTime fechaResena;
    private String nombreUsuario; // Para mostrar quién hizo la reseña
    
    @NotNull(message = "El ID del producto es necesario.")
    private Long productoId;
    private Long usuarioId; 
}