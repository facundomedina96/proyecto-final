/*
 * 
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
            List<Usuario> propietarios = usuarioServicio.buscarPropietariosActivos();
            model.put("propietarios", propietarios);
            List<Propiedad> propiedades = propiedadServicio.buscarPorPropietariosActivos();
            model.put("propiedades", propiedades);
            return "inicio.html"; 
            
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error.html"; 
        }
    }

    @GetMapping("/listaPropiedades")
    public String listaPropiedades(ModelMap model) {
        try {
            List<Propiedad> propiedades = propiedadServicio.listarPropiedades();
            model.put("propiedades", propiedades);
            return "inicio.html"; 
            
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error.html"; 
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
    public String buscar(String idPropietario, String ciudad, Double precio, ModelMap modelo) {
        //inyectar nuevamente lista de propietarios
        List<Usuario> propietarios = usuarioServicio.buscarPropietarios();
        modelo.put("propietarios", propietarios);
        //buscar resultados segun filtros seleccionados    
        List<Propiedad> resultados = new ArrayList<>();
        if (!"n".equals(idPropietario) && !"n".equals(ciudad) && precio != 0) {
            resultados = propiedadServicio.buscarPorPropietarioCiudadPrecio(idPropietario, ciudad, precio);
        } else if (!"n".equals(idPropietario) && !"n".equals(ciudad) && precio == 0) {
            resultados = propiedadServicio.buscarPorPropietarioYCiudad(idPropietario, ciudad);
        } else if (!"n".equals(idPropietario) && ciudad.equals("n") && precio != 0) {
            resultados = propiedadServicio.buscarPorPropietarioYPrecio(idPropietario, precio);
        } else if (!"n".equals(idPropietario) && ciudad.equals("n") && precio == 0) {
            resultados = propiedadServicio.buscarPropiedadPorPropietario(idPropietario);
        } else if (idPropietario.equals("n") && !"n".equals(ciudad) && precio != 0) {
            resultados = propiedadServicio.buscarPorCiudadYPrecio(ciudad, precio);
        } else if (!"n".equals(ciudad)) {
            resultados = propiedadServicio.buscarPorCiudad(ciudad);
        } else if (precio != 0) {
            resultados = propiedadServicio.buscarPorPrecioMax(precio);
        } else {
            return "redirect:/";
        }
        modelo.put("propiedades", resultados);
        return "inicio.html";
    }

}
