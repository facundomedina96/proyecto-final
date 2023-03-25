/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.servicios.PropiedadServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Hernan E Encizo
 */
@Controller
@RequestMapping("/")
public class PortalControlador {

    private final PropiedadServicio propiedadServicio;

    public PortalControlador(PropiedadServicio propiedadServicio) {
        this.propiedadServicio = propiedadServicio;
    }

    @GetMapping("/") // especificamos la ruta donde interactua el usuario
    public String inicio(ModelMap model) {
        try {
            // Necesito inyectar en el HTML la lista de propiedades
            List<Propiedad> propiedades = propiedadServicio.listarPropiedades();
            model.put("propiedades", propiedades);

            // retorno del HTML
            return "inicio.html"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error.html"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @GetMapping("/listaPropiedades") // especificamos la ruta donde interactua el usuario
    public String listaPropiedades(ModelMap model) {
        try {
            List<Propiedad> propiedades = propiedadServicio.listarPropiedades(); // buscar todas las noticias
            model.put("propiedades", propiedades);
            // retorno del HTML
            return "inicio.html"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error.html"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @GetMapping("/detalle/{id}")
    public String detallePropiedad(ModelMap model, @PathVariable("id") String id) {
        try {
            Propiedad propiedad = propiedadServicio.getOne(id);
            model.put("propiedad", propiedad);
            return "detalle.html";

        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error.html";
        }
    }

}
