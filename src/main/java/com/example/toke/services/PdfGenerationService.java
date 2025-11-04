package com.example.toke.services;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.example.toke.dto.PedidoDetalleDTO; // Reutilizamos el DTO de detalle

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGenerationService {

    private final TemplateEngine templateEngine;

    @Autowired
    public PdfGenerationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generarBoletaPdf(PedidoDetalleDTO pedido) throws IOException {
        // 1. Prepara el contexto de Thymeleaf con los datos del pedido
        Context context = new Context();
        context.setVariable("pedido", pedido);

        // 2. Procesa la plantilla HTML (boleta.html) y la convierte en un String
        String html = templateEngine.process("pdf/boleta", context);

        // 3. Usa OpenHTMLtoPDF para convertir el String HTML a un PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (outputStream) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        }
    }
}