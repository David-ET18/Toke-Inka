package com.example.toke.repositories;

import com.example.toke.entities.Usuario;
import com.example.toke.entities.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByRol(RolUsuario rol);

    boolean existsByEmail(String email);
}