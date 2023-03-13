/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Propiedad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Hernan E Encizo
 */
public interface PropiedadRepositorio extends JpaRepository<Propiedad, String> {
    // query para traer todas las propiedades pertecientes a un propietario en particular.
    @Query("SELECT p FROM Propiedad p WHERE p.propietario.id = :id")
    public List<Propiedad> buscarPorPropietario(@Param ("id") String id);
}
