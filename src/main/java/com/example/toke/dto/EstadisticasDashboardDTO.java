package com.example.toke.dto;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder // El patrón Builder es muy conveniente para objetos complejos como este
public class EstadisticasDashboardDTO {
    private long totalUsuarios;
    private long totalProductos;
    private long totalPedidos;
    private BigDecimal ingresosTotales;
    private Map<String, BigDecimal> ingresosPorCategoria; // Ej: {"Camisetas": 1500.00, "Pantalones": 2300.50}
    private List<ProductoResumenDTO> productosMasVendidos;
}