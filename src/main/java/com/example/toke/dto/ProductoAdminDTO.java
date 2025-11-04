package com.example.toke.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Data
public class ProductoAdminDTO {
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacío.")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0.")
    private BigDecimal precio;

    @NotNull(message = "La categoría es obligatoria.")
    private Long categoriaId;
    
    // Campo para la subida de la imagen
    private MultipartFile imagen;
    
    // Campo para mostrar la URL de la imagen actual al editar
    private String urlImagenActual;
}