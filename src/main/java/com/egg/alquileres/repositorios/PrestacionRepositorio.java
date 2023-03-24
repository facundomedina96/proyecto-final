package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Prestacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestacionRepositorio extends JpaRepository<Prestacion, String> {
    
}
