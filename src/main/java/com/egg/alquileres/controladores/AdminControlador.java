package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.UsuarioServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author milip
 */
@Controller
//@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin")
public class AdminControlador {

    @Autowired
    UsuarioServicio usuarioServicio;
    @Autowired
    PropiedadServicio propiedadServicio;

    @GetMapping("/dashboard")
    public String panelAdmin() {
        return "panel.html";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(ModelMap modelo) {
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        modelo.addAttribute("usuarios", usuarios);
        return "usuario_list";
    }

    @GetMapping("/eliminaruser/{id}")
    public String eliminarUsuario(@PathVariable String id) throws MiException {
        usuarioServicio.eliminar(id);
        return "redirect:/admin/usuarios";
    }

    //@GetMapping("/propiedades")
//    public String listarPropiedades(ModelMap modelo) {
//        List<Propiedad> propiedades = propiedadServicio.listarPropiedades();
//        modelo.addAttribute("propiedades", propiedades);
//        return "index.html";
//    }  ACA REUTILICE EL INDEX

    @GetMapping("/eliminarpropiedad/{id}")
    public String eliminarPropiedad(@PathVariable String id) throws MiException {
        propiedadServicio.eliminarPropiedad(id);
        return "redirect:/";
    }

}
