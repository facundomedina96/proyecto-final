package com.egg.alquileres.controladores;

import com.egg.alquileres.enumeraciones.Rol;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

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
    public String registro(ModelMap model, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String email, @RequestParam String password, @RequestParam String password2, @RequestParam String telefono, @RequestParam Rol rol) {
        try {
              
            usuarioServicio.registrar(nombre, apellido, email, password, password2, telefono,rol);

            model.put("exito", "Ya puedes ingresar con tu correo y contraseña");

            return "index";
        } catch (MiException e) {
            model.put("error", e.getMessage());
            return "usuario_form"; // mas tarde crearemos un html para mostrar si surge errores
        }
    }

}
