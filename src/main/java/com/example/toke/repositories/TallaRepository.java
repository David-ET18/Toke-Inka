package com.example.toke.repositories;

import com.example.toke.entities.Talla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TallaRepository extends JpaRepository<Talla, Long> {}