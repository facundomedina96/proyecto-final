/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.UsuarioServicio;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    UsuarioServicio usuarioServicio;

    public PortalControlador(PropiedadServicio propiedadServicio) {
        this.propiedadServicio = propiedadServicio;
    }

    @GetMapping("/") // especificamos la ruta donde interactua el usuario
    public String inicio(ModelMap model) {
        try {
            List<Usuario> propietarios = usuarioServicio.buscarPropietarios();
            model.put("propietarios", propietarios);
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

    @GetMapping("/busqueda-filtrada")
    public String buscar(String idPropietario, ModelMap modelo, String ciudad) {
        //inyectar nuevamente lista de propietarios
        List<Usuario> propietarios = usuarioServicio.buscarPropietarios();
            modelo.put("propietarios", propietarios);
        //buscar resultados segun filtros seleccionados    
        List<Propiedad> resultados = new ArrayList<>();
        if (!"n".equals(idPropietario) && !"n".equals(ciudad)) {
            resultados = propiedadServicio.buscarPorPropietarioYCiudad(idPropietario, ciudad);
        } else if (!"n".equals(idPropietario)) {
            resultados = propiedadServicio.buscarPropiedadPorPropietario(idPropietario);
        } else if (!"n".equals(ciudad)) {
            resultados = propiedadServicio.buscarPorCiudad(ciudad);
        }
        modelo.put("propiedades", resultados);
        return "inicio.html";
    }

}
