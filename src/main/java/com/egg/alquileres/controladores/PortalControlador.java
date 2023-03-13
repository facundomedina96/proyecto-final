/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.servicios.ClienteServicio;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private PropiedadServicio propiedadServicio;
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/") // especificamos la ruta donde interactua el usuario
    public String index(ModelMap model) {
        try {
            //Necesito inyectar en el HTML la lista de propiedades
            List<Propiedad> propiedades = propiedadServicio.listarPropiedades(); // buscar todas las noticias
            model.put("propiedades", propiedades); 
            
            
            List<Usuario> usuarios = usuarioServicio.listarUsuarios();
            model.put("usuarios", usuarios);
            //retorno del HTML
            return "index.html"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }
    
    @GetMapping("/inicio") // especificamos la ruta donde interactua el usuario
    public String inicio(ModelMap model, HttpSession session) {
        
        try {             
            Usuario logueado = (Usuario) session.getAttribute("usuarioSession");
            
            if(logueado.getRol().toString().equals("PROPIETARIO")){
                return "redirect:/propietario/dashboard";
            }else{
                return "redirect:/cliente/dashboard"; // si es usuario va a inicio sino dashboard
            }
           
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

//    @GetMapping("/listaPropiedades") // especificamos la ruta donde interactua el usuario
//    public String listaPropiedades(ModelMap model) {
//        try {
//            List<Propiedad> propiedades = propiedadServicio.listarPropiedades(); // buscar todas las noticias
//            model.put("propiedades", propiedades); // agregamos al model la propiedad "noticias" y la variable
//
//            return "propiedades_list"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.
//        } catch (Exception e) {
//            model.put("error", e.getMessage());
//            return "error"; // mas tarde crearemos un html para mostrar si surge errores
//        }
//    }

    @GetMapping("/detalle/{id}")
    public String detalleNoticia(ModelMap model, @PathVariable("id") String id) {
        try {
            Propiedad propiedad = propiedadServicio.getOne(id);
            model.put("propiedad", propiedad);
            return "detalle.html";

        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error";
        }
    }
}
