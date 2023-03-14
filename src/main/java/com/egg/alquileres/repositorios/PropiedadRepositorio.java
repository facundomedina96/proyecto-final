package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Propiedad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropiedadRepositorio extends JpaRepository<Propiedad, String> {

    @Query("SELECT p FROM Propiedad p WHERE p.propietario.id = :id")
    public List<Propiedad> buscarPorPropietario(@Param("id") String id);
}
