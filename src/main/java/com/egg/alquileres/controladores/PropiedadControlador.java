/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Comentario;
import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Reserva;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.ComentarioServicio;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.ReservaServicio;
import com.egg.alquileres.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Hernan E Encizo
 */
@Controller
//@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@RequestMapping("/propiedad")
public class PropiedadControlador {

    private final PropiedadServicio propiedadServicio;
    private final UsuarioServicio usuarioServicio;
    private final ComentarioServicio comentarioServicio;
    private final ReservaServicio reservaServicio;

    public PropiedadControlador(PropiedadServicio propiedadServicio, 
            UsuarioServicio usuarioServicio, ComentarioServicio comentarioServicio,
            ReservaServicio reservaServicio) {
        this.propiedadServicio = propiedadServicio;
        this.usuarioServicio = usuarioServicio;
        this.comentarioServicio = comentarioServicio;
        this.reservaServicio = reservaServicio;
    }

    // Metodo listarPropiedades toma un usuario y lista sus porpiedads en una tabla
    // crud;
    @GetMapping("/listar")
    public String listar(ModelMap model, HttpSession session) {
        List<Propiedad> propiedades = propiedadServicio.listarPropiedades();
        model.put("propiedades", propiedades);
        return "propiedades_crud.html";
    }

    // Metodo listarPropiedades toma un usuario y lista sus porpiedads en una tabla;
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, ModelMap model) {

        // obtengo informacion de la session y lo almaceno en sesionActual
        Propiedad propiedad = propiedadServicio.getOne(id);
        model.put("propiedad", propiedad);
        return "propiedad_modificar.html";
    }

    @PostMapping("/modificando/{id}")
    public String modificando(ModelMap modelo, @PathVariable String id, String nombre, String direccion, String ciudad,
            Double precio, MultipartFile fotos) {
        try {

            propiedadServicio.modificarPropiedad(id, nombre, direccion, ciudad, precio, fotos);
            modelo.put("exito", "Se ha modificado la propiedad con exito");

            return "redirect:/propiedad/listar";

        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "propiedades_crud.html";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(ModelMap modelo, @PathVariable String id) {
        try {
            propiedadServicio.eliminarPropiedad(id);
            modelo.put("exito", "Se ha eliminado la propiedad con exito");

            return "redirect:/propiedad/listar";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:../perfil";
        }
    }
    
    @GetMapping("/detalle/{id}")
    public String agregar_comentarios(ModelMap modelo, @PathVariable String id) {
        try {
            Reserva reserva = reservaServicio.getOne(id);
            if (reserva == null) {
                throw new MiException("Reserva nula.");
            }
            modelo.put("reserva", reserva);
            return "comentario.html";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:../";
        }
    }
    
    @PostMapping("/agregar_comentario/{id}")
    public String agregarComentario(ModelMap modelo, @PathVariable String id, int calificacion, String opinion, MultipartFile imagen) {
        try {
            Comentario comentario = comentarioServicio.crearComentario(opinion, calificacion, imagen);
            reservaServicio.agregarComentarioAReserva(id, comentario);

            return "redirect:/propiedad/listar";

        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "propiedades_crud.html";
        }
    }

}
