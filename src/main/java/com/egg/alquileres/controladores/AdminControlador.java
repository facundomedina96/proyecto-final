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
@PreAuthorize("hasRole('ROLE_ADMIN')")
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
    public String eliminarUsuario(ModelMap modelo, @PathVariable String id){
        try {
            usuarioServicio.eliminar(id);
            modelo.put("exito", "Se ha eliminado el usuario con exito");
            return "redirect:/admin/usuarios";
        } catch (MiException ex) {
            modelo.put("error", "No se ha eliminado el usuario, vuelve a intentarlo");
            return "redirect:/admin/usuarios";
        }
    }

    @GetMapping("/eliminarpropiedad/{id}")
    public String eliminarPropiedad(ModelMap modelo, @PathVariable String id){
        try {
            propiedadServicio.eliminarPropiedad(id);
            modelo.put("exito", "Se ha eliminado la propiedad con exito");
            return "redirect:/";
        } catch (MiException ex) {
            modelo.put("error", "No se ha eliminado la propiedad, vuelve a intentarlo");
            return "redirect:/";
        }
    }

}
