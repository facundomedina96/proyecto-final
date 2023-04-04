package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Prestacion;
import com.egg.alquileres.repositorios.PrestacionRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrestacionServicio {
    @Autowired
    PrestacionRepositorio prestacionRepo;
    
    public Prestacion crearPrestacion(String nombre, Double precio, Boolean activo){
        Prestacion prestacion = new Prestacion();
        prestacion.setNombre(nombre);
        prestacion.setPrecio(precio);
        prestacion.setActivo(activo);
        
        prestacionRepo.save(prestacion);
        
        return prestacion;
    }
    //vista del propietario para que pueda modificar la prestacion de su propiedad
    public void modificarPrestacion(String id, String nombre, Double precio){
        Optional<Prestacion> respuesta = prestacionRepo.findById(id);
        
        if(respuesta.isPresent()){
            Prestacion prestacion = respuesta.get();
            prestacion.setNombre(nombre);
            prestacion.setPrecio(precio);


            prestacionRepo.save(prestacion);
        }
        
    }
}
