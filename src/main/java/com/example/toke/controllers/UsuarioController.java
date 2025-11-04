package com.example.toke.controllers;

import com.example.toke.dto.UsuarioPerfilDTO;
import com.example.toke.entities.Usuario;
import com.example.toke.repositories.UsuarioRepository;
import com.example.toke.services.PedidoService;
import com.example.toke.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders; // Importa HttpHeaders
import org.springframework.http.MediaType;   // Importa MediaType
import org.springframework.http.ResponseEntity; // Importa ResponseEntity
import com.example.toke.services.PdfGenerationService;
import com.example.toke.dto.PedidoDetalleDTO;// Importa el nuevo servicio
import java.io.IOException;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final UsuarioRepository usuarioRepository;
    private final PdfGenerationService pdfService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, PedidoService pedidoService, UsuarioRepository usuarioRepository,PdfGenerationService pdfService) {
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.usuarioRepository = usuarioRepository;
        this.pdfService = pdfService;
    }

    @GetMapping("/mi-cuenta")
    public String verMiCuenta(Model model, Authentication authentication, HttpServletRequest request) {
        String email = authentication.getName();
        UsuarioPerfilDTO perfil = usuarioService.obtenerPerfilPorEmail(email);
        
        model.addAttribute("perfil", perfil);
        model.addAttribute("requestURI", request.getRequestURI());
        return "cliente/mi-cuenta";
    }

    @PostMapping("/mi-cuenta/actualizar")
    public String actualizarMiCuenta(@Valid @ModelAttribute("perfil") UsuarioPerfilDTO perfilDTO,
                                   BindingResult result,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("requestURI", request.getRequestURI());
            return "cliente/mi-cuenta";
        }
        
        usuarioService.actualizarPerfil(perfilDTO, authentication.getName());
        redirectAttributes.addFlashAttribute("mensajeExito", "¡Tu perfil ha sido actualizado con éxito!");
        return "redirect:/mi-cuenta";
    }

    @GetMapping("/mis-pedidos")
    public String verMisPedidos(Model model, Authentication authentication, HttpServletRequest request) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        
        model.addAttribute("pedidos", pedidoService.obtenerPedidosPorUsuario(usuario.getId()));
        model.addAttribute("requestURI", request.getRequestURI());
        return "cliente/mis-pedidos";
    }

    @GetMapping("/mis-pedidos/{id}")
    public String verDetallePedido(@PathVariable Long id, Model model, Authentication authentication, HttpServletRequest request) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        
        // El servicio se encarga de la validación de seguridad
        model.addAttribute("pedido", pedidoService.obtenerDetallePedidoParaCliente(id, usuario.getId()));
        model.addAttribute("requestURI", request.getRequestURI());
        return "cliente/pedido-detalle";
    }
     @GetMapping("/mis-pedidos/{id}/boleta")
    public ResponseEntity<byte[]> descargarBoleta(@PathVariable Long id, Authentication authentication) throws IOException {
        // 1. Obtenemos los datos del pedido de forma segura
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        PedidoDetalleDTO pedido = pedidoService.obtenerDetallePedidoParaCliente(id, usuario.getId());

        // 2. Generamos el PDF usando el servicio
        byte[] pdfBytes = pdfService.generarBoletaPdf(pedido);

        // 3. Preparamos la respuesta HTTP para el navegador
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // El nombre del archivo que se descargará
        String filename = "boleta-pedido-" + id + ".pdf";
        // 'inline' intenta mostrarlo en el navegador, 'attachment' fuerza la descarga.
        headers.setContentDispositionFormData("inline", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}