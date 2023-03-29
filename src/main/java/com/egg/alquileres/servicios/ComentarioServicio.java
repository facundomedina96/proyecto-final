/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Comentario;
import com.egg.alquileres.repositorios.ComentarioRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author agofa
 */
@Service
public class ComentarioServicio {
    @Autowired
    ComentarioRepositorio comentarioRepositorio;
    
    public String crearComentario(String opinion){
        Comentario comentario = new Comentario();
        comentario.setOpinion(opinion);
        
        comentarioRepositorio.save(opinion);
        return opinion;
    }
    
    public void modificarComentario(String id, String opinion){
        Optional <Comentario> respuesta = comentarioRepositorio.findById(id);
        
        if(respuesta.isPresent()){
            Comentario comentario = respuesta.get();
            comentario.setOpinion(opinion);
     
            comentarioRepositorio.save(comentario);
        }
        
    }
    
    public void eliminarComentario(String id, String opinion){
        Optional <Comentario> respuesta = comentarioRepositorio.findById(id);
        
        if(respuesta.isPresent()){
            Comentario comentario = respuesta.get();
 
     
            comentarioRepositorio.deleteById(id);
        }
    }

    Comentario crearComentario(Comentario opinion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
