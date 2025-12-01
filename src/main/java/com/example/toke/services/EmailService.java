package com.example.toke.services;

import com.example.toke.dto.PedidoDetalleDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // IMPORTANTE
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

    // 1. INYECTAMOS EL CORREO DE BREVO DESDE LA CONFIGURACIÓN
    // Esto tomará el valor de la variable MAIL_USERNAME de Render
    @Value("${spring.mail.username}")
    private String remitente;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, PdfGenerationService pdfService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.pdfService = pdfService;
    }

    @Async // Se ejecuta en segundo plano para no trabar la web
    public void enviarBoletaPorCorreo(PedidoDetalleDTO pedido, String emailCliente) {
        try {
            // Preparar mensaje
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Preparar datos para el HTML
            Context context = new Context();
            context.setVariable("pedido", pedido);
            
            // 2. CORRECCIÓN DE NOMBRE DE PLANTILLA
            // Debe coincidir con el nombre del archivo en resources/templates/email/
            // Usamos 'correo-compra' que fue el diseño "Ultra" que hicimos.
            String htmlBody = templateEngine.process("email/confirmacion-pedido", context);

            // Configurar destinatario y asunto
            helper.setTo(emailCliente);
            
            // 3. CORRECCIÓN DEL REMITENTE (Crucial para Brevo)
            // Usamos la variable inyectada, no un string "hardcoded"
            helper.setFrom(remitente); 
            
            helper.setSubject("Confirmación de Pedido #" + pedido.getId() + " - Toke Inca");
            helper.setText(htmlBody, true);

            // Generar y adjuntar PDF
            byte[] boletaPdf = pdfService.generarBoletaPdf(pedido);
            helper.addAttachment("Boleta-" + pedido.getId() + ".pdf", new ByteArrayResource(boletaPdf));

            // Enviar
            mailSender.send(mimeMessage);
            
            // Log de éxito para ver en Render
            System.out.println("✅ Correo enviado con éxito a: " + emailCliente + " desde: " + remitente);

        } catch (MessagingException | IOException e) {
            // Log de error detallado
            System.err.println("❌ ERROR CRÍTICO enviando correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
