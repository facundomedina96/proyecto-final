package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.UsuarioServicio;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Hernan E Encizo
 */
@Controller
@PreAuthorize("hasAnyRole('ROLE_CLIENTE')")
@RequestMapping("/cliente")
public class ClienteControlador {

    private final UsuarioServicio usuarioServicio;

    public ClienteControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }
    
    @GetMapping("/dashboard")
    public String panelCliente(ModelMap modelo) {
        return "panel.html";
    }

    @GetMapping("/perfil")
    public String perfil(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
        
        modelo.put("usuario", usuario);
        return "usuarioPerfil.html";
    }

    @GetMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @PathVariable String id) {
        // inyeccion en el html del usuario para mostrar sus datos.
        modelo.put("cliente", usuarioServicio.getOne(id));
        return "clienteModificarPerfil.html";
    }

    @PostMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @PathVariable String id, String nombre, String apellido, String email, String password, String password2, String telefono) {
        try {
            usuarioServicio.modificar(id, nombre, apellido, email, password, password2, telefono);
            modelo.put("exito", "Se ha modificado su perfil con exito");

            return "redirect:/login";
            //return "noticia_list.html";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "clienteModificarPerfil.html";
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
