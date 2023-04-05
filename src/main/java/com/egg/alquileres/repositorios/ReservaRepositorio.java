package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Reserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, String> {

    @Query("SELECT r FROM Reserva r WHERE r.cliente.id = :id AND r.estado != 1")
    public List<Reserva> buscarPorCliente(@Param("id") String id);
    
    @Query("SELECT r FROM Reserva r WHERE r.cliente.activo = TRUE")
    public List<Reserva> buscarClientesActivos();
    
    @Query("SELECT r FROM Reserva r WHERE r.propiedad.id = :id AND r.cliente.activo = TRUE")
    public List<Reserva> listarReservasDeUnaPropiedad(@Param("id") String id);
}
