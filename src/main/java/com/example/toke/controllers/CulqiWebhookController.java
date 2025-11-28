package com.example.toke.controllers;

import com.example.toke.services.PedidoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
public class CulqiWebhookController {

    private final PedidoService pedidoService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CulqiWebhookController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        this.objectMapper = new ObjectMapper(); // Herramienta de Spring para leer JSON
    }

    @PostMapping("/culqi")
    public ResponseEntity<Void> handleCulqiWebhook(@RequestBody String payload) {
        System.out.println("====== WEBHOOK DE CULQI RECIBIDO ======");
        // Imprimimos el payload para depurar y ver qué nos manda Culqi
        System.out.println(payload);
        
        try {
            // Leemos el JSON recibido
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.path("type").asText();

            // Verificamos si es un evento de cambio de estado de orden
            if ("order.status.changed".equals(eventType)) {
                JsonNode dataNode = rootNode.path("data");
                
                // En el objeto 'data', el campo 'state' nos dice si ya se pagó
                String state = dataNode.path("state").asText();

                if ("paid".equals(state)) {
                    // Obtenemos nuestro número de orden personalizado (ej: toke-15-17823812)
                    String orderNumber = dataNode.path("order_number").asText();
                    
                    // Extraemos el ID numérico de nuestro sistema
                    // El formato es: toke-{ID}-{TIMESTAMP}
                    String[] partes = orderNumber.split("-");
                    if (partes.length >= 2) {
                        Long pedidoId = Long.parseLong(partes[1]);
                        
                        System.out.println("Pago confirmado para el Pedido ID: " + pedidoId);
                        
                        // Ejecutamos la lógica de negocio (bajar stock, enviar correo)
                        pedidoService.confirmarPedidoPagado(pedidoId);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando webhook: " + e.getMessage());
            e.printStackTrace();
            // No devolvemos error 500 para evitar que Culqi reintente infinitamente si es un error de lógica nuestra
        }

        // Siempre respondemos OK a Culqi
        return ResponseEntity.ok().build();
    }
}