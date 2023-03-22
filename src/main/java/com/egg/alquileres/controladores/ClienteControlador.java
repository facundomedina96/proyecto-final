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
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @PathVariable String id) {
        modelo.put("usuario", usuarioServicio.getOne(id));
        return "cliente_modificar_perfil.html";
    }

    @PostMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @PathVariable String id,
            String nombre, String apellido, String email, String password,
            String password2, String telefono, MultipartFile foto_perfil) {
        try {
            usuarioServicio.modificar(id, nombre, apellido, email, password, password2, telefono, foto_perfil);
            modelo.put("exito", "Se ha modificado su perfil con exito");

            return "redirect:/iniciar_sesion";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "cliente_modificar_perfil.html";
        }
    }

    @GetMapping("/eliminarPerfil/{id}")
    public String eliminarPerfil(ModelMap modelo, @PathVariable String id, 
            HttpSession session) {
        try {
            // inyeccion en el html del usuario para mostrar sus datos.
            usuarioServicio.eliminar(id);
            session.invalidate();
            modelo.put("exito", "Se ha eliminado su perfil con exito");
            return "redirect:/";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "redirect:../perfil";
        }
    }
}
