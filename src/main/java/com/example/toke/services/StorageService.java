package com.example.toke.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    // Define la ruta a la carpeta de subidas.
    private final Path rootLocation = Paths.get("uploads/products");

    // Constructor para crear la carpeta si no existe.
    public StorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de almacenamiento de archivos.", e);
        }
    }

    /**
     * Guarda un archivo subido.
     * @param file El archivo MultipartFile recibido del formulario.
     * @return El nombre único generado para el archivo guardado.
     */
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("No se puede guardar un archivo vacío.");
        }

        try {
            // Genera un nombre de archivo único para evitar colisiones
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Copia el archivo a la carpeta de destino
            Files.copy(file.getInputStream(), this.rootLocation.resolve(uniqueFilename));

            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Fallo al guardar el archivo.", e);
        }
    }
}