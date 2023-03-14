/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.UsuarioServicio;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Hernan E Encizo
 */
@Controller
@PreAuthorize("hasAnyRole('ROLE_PROPIETARIO')")
@RequestMapping("/propiedad")
public class PropiedadControlador {

    @Autowired
    private PropiedadServicio propiedadServicio;
    @Autowired
    private UsuarioServicio propietarioServicio;

    @GetMapping("/registrar") // especificamos la ruta donde interactua el usuario
    public String registrar(ModelMap model, HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            model.put("usuario", usuario.getId());

            return "propiedadRegistro.html"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.

        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "redirect:/propietario/dashboard"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @PostMapping("/registro/{id}") // especificamos la ruta donde interactua el usuario
    public String registro(ModelMap model, @RequestParam String nombre, @RequestParam String direccion, @RequestParam String ciudad, @RequestParam Double precio, String id) {

        try {
            Usuario propietario = propietarioServicio.getOne(id);

            System.out.println("El nombre del propietario es: " + propietario.getNombre());
            propiedadServicio.crearPropiedad(nombre, direccion, ciudad, precio, propietario);

            model.put("exito", "Propiedad registrada con exito");
            return "redirect:/propietario/dashboard";
        } catch (MiException ex) {
            model.put("error", ex.getMessage());
            return "redirect:/propiedad/registrar";
        } catch (ParseException ex) {
            model.put("error", ex.getMessage());
            return "redirect:/propiedad/registrar";
        }
    }
}
