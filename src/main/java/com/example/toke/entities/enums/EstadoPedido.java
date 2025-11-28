package com.example.toke.entities.enums;

public enum EstadoPedido {
    PENDIENTE_PAGO, // El pedido ha sido creado, pero aún no se ha pagado.
    PAGADO,         // El pago ha sido confirmado exitosamente.
    ENVIADO,        // El pedido ha sido despachado.
    COMPLETADO,     // El pedido ha sido entregado.
    CANCELADO,      // El pedido ha sido cancelado.
    ERROR_STOCK     // Ocurrió un error de stock durante la confirmación del pago.
}