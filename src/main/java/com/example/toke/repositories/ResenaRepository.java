package com.example.toke.repositories;

import com.example.toke.entities.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    
    // Para mostrar todas las reseñas de un producto
    List<Resena> findByProductoId(Long idProducto);
}