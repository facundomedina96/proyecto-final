package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.enumeraciones.Rol;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.UsuarioServicio;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/registrar") // especificamos la ruta donde interactua el usuario
    public String registrar(ModelMap model) {
        try {
            return "usuarioFormulario"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "error"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @PostMapping("/registro") // especificamos la ruta donde interactua el usuario
    public String registro(ModelMap model, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String email, @RequestParam String password, @RequestParam String password2, @RequestParam String telefono, @RequestParam Rol rol, @RequestParam MultipartFile foto_perfil) throws MiException {
        try {

            usuarioServicio.registrar(nombre, apellido, email, password, password2, telefono, rol, foto_perfil);

            model.put("exito", "Ya puedes ingresar con tu correo y contraseña");

            return "index";
        } catch (MiException e) {
            model.put("error", e.getMessage());
            return "usuario_form"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @GetMapping("/dashboard")
    public String panel(ModelMap modelo) {
        return "panel.html";
    }

    @GetMapping("/login") // especificamos la ruta donde interactua el usuario
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        try {
            if (error != null) {
                modelo.put("error", "Usuario o contraseña invalido!");
            }
            return "usuario_login"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
            return "index"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @GetMapping("/perfil")
    public String perfil(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        modelo.put("usuario", usuario);
        return "usuarioPerfil.html";
    }
}
