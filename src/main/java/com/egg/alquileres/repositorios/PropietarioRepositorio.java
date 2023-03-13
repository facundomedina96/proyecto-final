/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Hernan E Encizo
 */
@Repository
public interface PropietarioRepositorio extends JpaRepository<Propietario, String>{
    
}
