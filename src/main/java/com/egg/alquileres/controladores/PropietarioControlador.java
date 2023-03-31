/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAnyRole('ROLE_PROPIETARIO')")
@RequestMapping("/propietario")
public class PropietarioControlador {

    private final UsuarioServicio usuarioServicio;
    private final PropiedadServicio propiedadServicio;

    public PropietarioControlador(UsuarioServicio propietarioServicio, PropiedadServicio propiedadServicio) {
        this.usuarioServicio = propietarioServicio;
        this.propiedadServicio = propiedadServicio;
    }

    //Metodo propiedadesCRUD lista las porpiedads en una tabla crud(puede modificar y eliminar);
    @GetMapping("/misPropiedades")
    public String misPropiedades(ModelMap model, HttpSession session) {
        try {
            
            Usuario sesionActual = (Usuario) session.getAttribute("usuarioSession");
            List<Propiedad> propiedades = usuarioServicio.listarPropiedades(sesionActual.getId());
            
            // inyeccion de las propiedades en una Tabla CRUD;
            model.put("propiedades", propiedades);
            return "propiedades_crud.html";
            
        } catch (MiException e) {
            model.put("error", e.getMessage());
            return "error.html";
        }
    }

    //Metodo GET modificarPropiedad recibe el id de la propiedad que se desea modificar; 
    @GetMapping("/modificarPropiedad/{id}")
    public String modificarPropiedad(@PathVariable String id, ModelMap model) {

        // Obtencion de la propiedad a modificar;
        Propiedad propiedad = propiedadServicio.getOne(id);
        
        //Inyeccion en el HTML de los datos de la propiedad a modificar
        model.put("propiedad", propiedad);
        // Retorno del Formulario de modificacion con los valores inyecetados
        return "propiedad_modificar.html";
    }

    //Metodo POST modificarPropiedad recepcion de los datos nuevos
    @PostMapping("/modificarPropiedad/{id}")
    public String modificarPropiedad(ModelMap modelo, @PathVariable String id, String nombre, String direccion, String ciudad, Double precio, MultipartFile fotos) {
        try {
            
            // llamado al metodo de modificar(envio de parametros recibidos)
            propiedadServicio.modificarPropiedad(id, nombre, direccion, ciudad, precio, fotos);
            modelo.put("exito", "Se ha modificado la propiedad con exito");

            return "redirect:/propietario/listarPropiedades";

        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "propiedades_crud.html";
        }
    }

    @GetMapping("/eliminarPropiedad/{id}")
    public String eliminarPropiedad(ModelMap modelo, @PathVariable String id) {
        try {
            propiedadServicio.eliminarPropiedad(id);
            modelo.put("exito", "Se ha eliminado la propiedad con exito");

            return "redirect:/propietario/listarPropiedades";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:../perfil";
        }
    }
}
