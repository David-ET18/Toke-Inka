package com.example.toke.repositories;

import com.example.toke.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Busca todos los pedidos de un usuario específico, ordenados por fecha descendente
    // Ideal para el historial de pedidos del cliente.
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(Long idUsuario);
    List<Pedido> findAllByOrderByFechaPedidoDesc();
}