package com.example.toke.services;

import com.example.toke.dto.PedidoDetalleDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final PdfGenerationService pdfService;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, PdfGenerationService pdfService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.pdfService = pdfService;
    }

    @Async // ¡Esta anotación hace que el método se ejecute en segundo plano!
    public void enviarBoletaPorCorreo(PedidoDetalleDTO pedido, String emailCliente) {
        try {
            // 1. Prepara el mensaje MIME
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // El 'true' indica que será un mensaje multipart (con adjuntos)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 2. Prepara el contexto de Thymeleaf para el cuerpo del correo
            Context context = new Context();
            context.setVariable("pedido", pedido);
            String htmlBody = templateEngine.process("email/confirmacion-pedido", context);

            // 3. Configura los detalles del correo
            helper.setTo(emailCliente);
            helper.setFrom("tu-correo@gmail.com"); // Debe ser el mismo que configuraste
            helper.setSubject("Confirmación de tu pedido en Toke Inca #" + pedido.getId());
            helper.setText(htmlBody, true); // El 'true' indica que el texto es HTML

            // 4. Genera el PDF y lo adjunta
            byte[] boletaPdf = pdfService.generarBoletaPdf(pedido);
            helper.addAttachment("Boleta-Pedido-" + pedido.getId() + ".pdf", new ByteArrayResource(boletaPdf));

            // 5. Envía el correo
            mailSender.send(mimeMessage);

        } catch (MessagingException | IOException e) {
            // En un sistema real, aquí deberías registrar el error en un log
            System.err.println("Error al enviar el correo de confirmación: " + e.getMessage());
        }
    }
}