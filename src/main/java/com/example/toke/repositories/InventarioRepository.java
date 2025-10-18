package com.example.toke.repositories;

import com.example.toke.entities.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    // Método para encontrar el stock de un producto en una talla específica
    Optional<Inventario> findByProductoIdAndTallaId(Long idProducto, Long idTalla);
}

