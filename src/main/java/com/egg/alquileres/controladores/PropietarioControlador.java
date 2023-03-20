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

    // Esto te lleva a un panel generico
    @GetMapping("/panel")
    public String panel(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
        modelo.put("usuario", usuario);

        modelo.put("propiedades", propiedadServicio.buscarPropiedadPorPropietario(usuario.getId()));
        return "panel.html";
    }

    @GetMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @PathVariable String id) {
        modelo.put("propietario", usuarioServicio.getOne(id));
        return "propietario_modificar_perfil.html";
    }

    @PostMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @PathVariable String id, String nombre, String apellido, String email, String password, String password2, String telefono, MultipartFile foto_perfil) {
        try {
            usuarioServicio.modificar(id, nombre, apellido, email, password, password2, telefono, foto_perfil);
            modelo.put("exito", "Se ha modificado su perfil con exito");

            return "redirect:/iniciar_sesion";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "propietario_modificar_perfil.html";
        }
    }

    @GetMapping("/eliminarPerfil/{id}")
    public String eliminarPerfil(ModelMap modelo, @PathVariable String id) {
        try {
            usuarioServicio.eliminar(id);
            modelo.put("exito", "Se ha eliminado su perfil con exito");
            return "redirect:/";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:../perfil";
        }
    }
}
