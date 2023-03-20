package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Reserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, String> {

    @Query("SELECT r FROM Reserva r WHERE r.cliente.id = :id")
    public List<Reserva> buscarPorCliente(String id);
    
}
