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
    public String registro(ModelMap modelo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String email, @RequestParam String password, @RequestParam String password2, @RequestParam String telefono, @RequestParam(required = false) Rol rol, @RequestParam MultipartFile foto_perfil) throws MiException {
        try {

            usuarioServicio.registrar(nombre, apellido, email, password, password2, telefono, rol, foto_perfil);

            modelo.put("exito", "Ya puedes ingresar con tu correo y contraseña");

            return "inicio";
        } catch (MiException e) {
            modelo.put("error", e.getMessage());

            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            modelo.put("telefono", telefono);
            return "usuario_formulario.html"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    // trabajo desde el ultimo commit 
    @GetMapping("/iniciarSesion") // especificamos la ruta donde interactua el usuario
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        try {
            if (error != null) {
                modelo.put("error", "Usuario o contraseña invalido!");
            }
            return "iniciar_sesion.html"; // indicamos el path de nuestra pagina. Vamos a templates a crearla.
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
            return "inicio.html"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

    @GetMapping("/dashboard")
    public String panel(ModelMap modelo, HttpSession session) throws MiException {
        Usuario sesionActual = (Usuario) session.getAttribute("usuarioSession");
        if (!sesionActual.getActivo()) {
            modelo.put("error", "Su cuenta ha sido dada de baja por infringir las normas");
            session.invalidate();
            return "iniciar_sesion.html";
        } else {

            if(sesionActual.getRol().toString().equals("PROPIETARIO")){
                
                List<Propiedad> propiedades = propiedadServicio.listarPropiedadesPorPropietario(sesionActual.getId()); 
                modelo.put("propiedades", propiedades); 
            }
            
            modelo.put("usuario", sesionActual);
            return "panel";
        }
    }

    @GetMapping("/perfil")
    public String perfil(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");

        modelo.put("usuario", usuario);
        return "usuario_perfil.html";
    }

}
