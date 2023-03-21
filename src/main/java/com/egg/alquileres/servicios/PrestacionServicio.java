package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Prestacion;
import com.egg.alquileres.enumeraciones.NombrePrestacion;
import com.egg.alquileres.repositorios.PrestacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrestacionServicio {
    @Autowired
    PrestacionRepositorio prestacionRepo;
    
    public Prestacion crearPrestacion(NombrePrestacion nombre, Double precio, Boolean activo){
        Prestacion prestacion = new Prestacion();
        prestacion.setNombre(nombre);
        prestacion.setPrecio(precio);
        prestacion.setActivo(activo);
        
        prestacionRepo.save(prestacion);
        
        return prestacion;
    }
}
