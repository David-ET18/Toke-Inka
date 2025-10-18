package com.example.toke.controllers;
import com.example.toke.dto.UsuarioRegistroDTO;
import com.example.toke.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "auth/login"; // Vista: resources/templates/auth/login.html
    }

    @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDTO());
        return "auth/register"; // Vista: resources/templates/auth/register.html
    }

    @PostMapping("/register")
    public String procesarRegistro(@Valid @ModelAttribute("usuario") UsuarioRegistroDTO registroDTO,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            usuarioService.registrarNuevoCliente(registroDTO);
        } catch (IllegalStateException e) {
            result.rejectValue("email", "email.existente", e.getMessage());
            return "auth/register";
        }

        redirectAttributes.addFlashAttribute("registroExitoso", "¡Te has registrado con éxito! Ahora puedes iniciar sesión.");
        return "redirect:/login";
    }
}