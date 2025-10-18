package com.example.toke.repositories;

import com.example.toke.entities.DetallePedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    /**
     * Consulta que agrupa los detalles de pedido por producto y suma las cantidades vendidas.
     * Devuelve una lista de arrays de objetos (Object[]), donde:
     * - object[0] es la entidad Producto
     * - object[1] es la cantidad total vendida (Long)
     * Se ordena de mayor a menor cantidad vendida.
     */
    @Query("SELECT dp.producto, SUM(dp.cantidad) as totalVendido FROM DetallePedido dp GROUP BY dp.producto ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos(Pageable pageable);
}
