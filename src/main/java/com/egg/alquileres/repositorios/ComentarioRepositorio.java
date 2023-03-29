/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author agofa
 */
@Repository
public interface ComentarioRepositorio extends JpaRepository<Comentario, String> {

    public void save(String opinion);
    
}
