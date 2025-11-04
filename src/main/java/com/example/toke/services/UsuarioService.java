package com.example.toke.services;

import com.example.toke.dto.UsuarioPerfilDTO;
import com.example.toke.dto.UsuarioRegistroDTO;
import com.example.toke.entities.Usuario;
import com.example.toke.entities.enums.RolUsuario;
import com.example.toke.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public UsuarioPerfilDTO obtenerPerfilPorEmail(String email) {
    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    
    UsuarioPerfilDTO dto = new UsuarioPerfilDTO();
    dto.setNombre(usuario.getNombre());
    dto.setApellido(usuario.getApellido());
    dto.setEmail(usuario.getEmail());
    dto.setDireccion(usuario.getDireccion());
    dto.setTelefono(usuario.getTelefono());
    
    return dto;
}

@Transactional
public void actualizarPerfil(UsuarioPerfilDTO perfilDTO, String email) {
    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            
    usuario.setNombre(perfilDTO.getNombre());
    usuario.setApellido(perfilDTO.getApellido());
    usuario.setDireccion(perfilDTO.getDireccion());
    usuario.setTelefono(perfilDTO.getTelefono());
    
    usuarioRepository.save(usuario);
}

}
