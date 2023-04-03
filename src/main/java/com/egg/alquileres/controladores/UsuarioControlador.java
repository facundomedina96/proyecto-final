package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.enumeraciones.Rol;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Hernan E Encizo
 */
@Controller
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;
    private final PropiedadServicio propiedadServicio;
    public UsuarioControlador(UsuarioServicio usuarioServicio, PropiedadServicio propiedadServicio) {
        this.usuarioServicio = usuarioServicio;
        this.propiedadServicio = propiedadServicio;
    }

    @GetMapping("/registrar") // especificamos la ruta donde interactua el usuario
    public String registrar(ModelMap model) {
        try {
            return "usuario_formulario.html"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error.html"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @PostMapping("/registro") // especificamos la ruta donde interactua el usuario
    public String registro(ModelMap modelo, @RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam String password, @RequestParam String password2,
            @RequestParam String telefono, @RequestParam(required = false) Rol rol,
            @RequestParam(required = false) MultipartFile foto_perfil) throws MiException {
        try {

            usuarioServicio.registrar(nombre, apellido, email, password, password2, telefono, rol, foto_perfil);

            modelo.put("exito", "Ya puedes ingresar con tu correo y contraseña");
            
            List<Propiedad> propiedades = propiedadServicio.listarPropiedades();
            modelo.put("propiedades", propiedades);

            return "inicio.html";
        } catch (MiException e) {
            modelo.put("error", e.getMessage());

            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            modelo.put("telefono", telefono);
            return "usuario_formulario.html"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @GetMapping("/dashboard")
    public String panel(ModelMap modelo, HttpSession session) {
        Usuario sesionActual = (Usuario) session.getAttribute("usuarioSession");
        if (!sesionActual.getActivo()) {
            modelo.put("error", "Su cuenta ha sido dada de baja por infringir las normas");
            session.invalidate();
            return "iniciar_sesion.html";
        } else {
            modelo.addAttribute("usuario", usuarioServicio.getOne(sesionActual.getId()));
            modelo.addAttribute("propiedades", propiedadServicio.buscarPropiedadPorPropietario(sesionActual.getId()));
            return "panel.html";
        }
    }

    // trabajo desde el ultimo commit 
    @GetMapping("/iniciar-sesion") // especificamos la ruta donde interactua el usuario
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        try {
            if (error != null) {
                modelo.put("error", "Usuario o contraseña invalido!");
            }
            return "iniciar_sesion.html"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
            return "iniciar_sesion.html"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @GetMapping("/perfil")
    public String perfil(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
        Usuario userbbdd = usuarioServicio.getOne(usuario.getId());
        modelo.put("usuario", userbbdd);
        return "usuario_perfil.html";
    }

    @GetMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @PathVariable String id) {
        // inyeccion en el html del usuario para mostrar sus datos.
        modelo.put("usuario", usuarioServicio.getOne(id));
        return "usuario_modificar_perfil.html";
    }

    @PostMapping("/modificarPerfil/{id}")
    public String modificarPerfil(ModelMap modelo, @RequestParam String id, String nombre, String apellido,
            String email, String password, String password2, String telefono, MultipartFile foto_perfil) {
        try {
            usuarioServicio.modificar(id, nombre, apellido, email, password, password2, telefono, foto_perfil);
            modelo.put("exito", "Se ha modificado su perfil con exito");

            return "redirect:/perfil";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "usuario_modificar_perfil.html";
        }
    }

    @GetMapping("/eliminarPerfil/{id}")
    public String eliminarPerfil(ModelMap modelo, @PathVariable String id, HttpSession session) {
        try {
            // inyeccion en el html del usuario para mostrar sus datos.
            usuarioServicio.eliminar(id);
            modelo.put("exito", "Se ha eliminado su perfil con exito");
            session.invalidate();
            return "inicio.html";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "usuario_modificar_perfil.html";
        }
    }
}
