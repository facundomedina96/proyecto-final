package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenRepositorio extends JpaRepository<Imagen, String> {
    
}
