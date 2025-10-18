package com.example.toke.services;

import com.example.toke.dto.ResenaDTO;
import com.example.toke.entities.Producto;
import com.example.toke.entities.Resena;
import com.example.toke.entities.Usuario;
import com.example.toke.exception.ProductoNoEncontradoException;
import com.example.toke.repositories.ProductoRepository;
import com.example.toke.repositories.ResenaRepository;
import com.example.toke.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ResenaService(ResenaRepository resenaRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.resenaRepository = resenaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ResenaDTO crearResena(ResenaDTO resenaDTO, String userEmail) {
        Producto producto = productoRepository.findById(resenaDTO.getProductoId())
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado para la reseña."));
        
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));

        // Aquí podrías añadir lógica para validar si el usuario compró el producto.

        Resena nuevaResena = new Resena();
        nuevaResena.setProducto(producto);
        nuevaResena.setUsuario(usuario);
        nuevaResena.setCalificacion(resenaDTO.getCalificacion());
        nuevaResena.setComentario(resenaDTO.getComentario());

        Resena guardada = resenaRepository.save(nuevaResena);
        return mapToResenaDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<ResenaDTO> obtenerResenasPorProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId).stream()
                .map(this::mapToResenaDTO)
                .collect(Collectors.toList());
    }
    
    private ResenaDTO mapToResenaDTO(Resena resena) {
        ResenaDTO dto = new ResenaDTO();
        dto.setId(resena.getId());
        dto.setCalificacion(resena.getCalificacion());
        dto.setComentario(resena.getComentario());
        dto.setFechaResena(resena.getFechaResena());
        dto.setProductoId(resena.getProducto().getId());
        if (resena.getUsuario() != null) {
            dto.setNombreUsuario(resena.getUsuario().getNombre() + " " + resena.getUsuario().getApellido().charAt(0) + ".");
        }
        return dto;
    }
}
