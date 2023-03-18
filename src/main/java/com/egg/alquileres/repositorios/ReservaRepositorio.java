package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, String> {
    
}
