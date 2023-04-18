/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Comentario;
import com.egg.alquileres.entidades.Imagen;
import com.egg.alquileres.repositorios.ComentarioRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Sofia Raia
 */
@Service
public class ComentarioServicio {
    @Autowired
    ComentarioRepositorio comentarioRepositorio;
    @Autowired
    ImagenServicio imagenServicio;
    
    public Comentario crearComentario(String opinion, int calificacion, MultipartFile imagen){
        Comentario comentario = new Comentario();
        comentario.setOpinion(opinion);
        comentario.setCalificacion(calificacion);
        Imagen img = imagenServicio.crearImagen(imagen);
        comentario.setImagen(img);
        comentarioRepositorio.save(comentario);
        return comentario;
    }
    
    public void modificarComentario(String id, String opinion, int calificacion){
        Optional <Comentario> respuesta = comentarioRepositorio.findById(id);
        
        if(respuesta.isPresent()){
            Comentario comentario = respuesta.get();
            comentario.setOpinion(opinion);
            comentario.setCalificacion(calificacion);
     
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

   


}
