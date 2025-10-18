package com.example.toke.repositories;

import com.example.toke.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Busca todos los productos de una categoría específica por el ID de la categoría
    List<Producto> findByCategoriaId(Long idCategoria);

    // Busca productos cuyo nombre contenga una cadena de texto, ignorando mayúsculas/minúsculas
    // Muy útil para una barra de búsqueda.
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}