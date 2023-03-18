/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.controladores;

import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.UsuarioServicio;
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

    public PropietarioControlador(UsuarioServicio propietarioServicio) {
        this.usuarioServicio = propietarioServicio;
    }

    @GetMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @PathVariable String id) {
        // inyeccion en el html del usuario para mostrar sus datos.
        modelo.put("propietario", usuarioServicio.getOne(id));
        return "propietarioModificarPerfil.html";
    }

    @PostMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @PathVariable String id, String nombre, String apellido, String email, String password, String password2, String telefono, MultipartFile foto_perfil) {
        try {
            usuarioServicio.modificar(id, nombre, apellido, email, password, password2, telefono, foto_perfil);
            modelo.put("exito", "Se ha modificado su perfil con exito");

            return "redirect:/login";
            //return "noticia_list.html";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "propietarioModificarPerfil.html";
        }
    }

    @GetMapping("/eliminarPerfil/{id}")
    public String eliminarPerfil(ModelMap modelo, @PathVariable String id) {
        try {
            // inyeccion en el html del usuario para mostrar sus datos.
            usuarioServicio.eliminar(id);
            modelo.put("exito", "Se ha eliminado su perfil con exito");
            return "redirect:/";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:../perfil";
        }
    }
}
