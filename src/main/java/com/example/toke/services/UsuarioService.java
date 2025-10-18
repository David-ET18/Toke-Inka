package com.example.toke.services;

import com.example.toke.dto.UsuarioRegistroDTO;
import com.example.toke.entities.Usuario;
import com.example.toke.entities.enums.RolUsuario;
import com.example.toke.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrarNuevoCliente(UsuarioRegistroDTO registroDTO) {
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new IllegalStateException("Ya existe una cuenta con el email: " + registroDTO.getEmail());
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setApellido(registroDTO.getApellido());
        nuevoUsuario.setEmail(registroDTO.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        nuevoUsuario.setRol(RolUsuario.ROLE_CLIENTE);

        return usuarioRepository.save(nuevoUsuario);
    }
}
