/*
    ------>>> TODO: Podria crear Clientes y Propietarios en el mismo Servicio utilizando if y tomar rutas 
    dependiendo de la eleccion que me llegue por parametro desde un formulario.
 */
package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author Luz
 */
@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private PropietarioServicio propietarioServicio;

    // MËTODO PARA VALIDAR LOS PARAMETROS RECIBIDOS DEL FORMULARIO
    public void validar(String nombre, String apellido, String email, String password, String password2, String telefono) throws MiException {
        if (nombre == null || nombre.isEmpty()) {
            throw new MiException("El nombre no puede ser nulo ni estar vacio.");
        }

        if (apellido == null || apellido.isEmpty()) {
            throw new MiException("El apellido no puede ser nulo ni estar vacio.");
        }

        if (email == null || email.isEmpty()) {
            throw new MiException("El Email no puede ser nulo ni estar vacio.");
        }
        if (password == null || password.isEmpty() || password.length() <= 5) {
            throw new MiException("La contraseña no puede ser nulo ni estar vacio y debe contener mas de 5 dígitos.");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas deben ser iguales");
        }
        if (telefono == null || telefono.isEmpty()) {
            throw new MiException("El numero de telefono no puede ser nulo ni estar vacio.");
        }
    }

    // METODO PARA REGISTRAR UN USUARIO
    @Transactional
    public void registrar(String nombre, String apellido, String email, String password, String password2, String telefono, String rol) throws MiException {
        // llamo al metodo validar pasando los parametros recibidos del form
        if (rol == null || rol.isEmpty()) {
            throw new MiException("El rol no puede ser nulo ni estar vacio.");
        }
        
        validar(nombre, apellido, email, password, password2, telefono);

        // Antes de registrar pregunto que rol tendra y llamo al correspondiente servicio
        if (rol.equalsIgnoreCase("cliente")) {
            clienteServicio.registrar(nombre, apellido, email, password, password2, telefono);
        } else {
            propietarioServicio.registrarPropietario(nombre, apellido, email, password, password2, telefono);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // en primer lugar vamos a buscar un usuario de nuestro dominio 
        // a traves del metodo que escribimos en nuestro repositorio
        // y transformarlo en un usuario del dominio de Spring Security.
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) { // si el usuario existe procedemos
            // instanciamos un objeto de la Clase User
            // el constructor de la clase nos solicitara varios parametros
            // un nombre de usuario, contraseña y una lista de permisos

            // para los permisos nos creamos una lista que almacene objetos de la clase GrantedAuthority
            List<GrantedAuthority> permisos = new ArrayList();

            //posteriormente creamos permisos para un usuario --> per; 
            // lo instanciamos como SimpleGrantedAuthority y dentro del constructor
            // especificamos a quien le vamos a dar esos permisos --> "ROLE_" + getRol().
            GrantedAuthority per = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            // luego agregamos esos permisos a la lista creada mas arriba
            permisos.add(per);

            // agregamos los permisos al constructor junto con email y la contraseña            
            User user = new User(usuario.getEmail(), usuario.getPassword(), permisos);

            // atrapamos al usuario que ya esta autenticado y guardarlo en la session.
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            //Guardamos la solicitud en un objeto de la interfaz HttpSession
            HttpSession session = attr.getRequest().getSession(true);

            // en los datos de la session seteamos los atributos
            // en la varible session vamos a setear el atributo usuarioSession como llave y lo que va a contener 
            // es el valor con todos los datos del objeto usuario autentificado.
            session.setAttribute("usuarioSession", usuario);

            // y retornamos ese Usuario.
            return user;
        } else {
            return null;
        }
    }

    public void modificar(String id, String nombre, String apellido, String email, String password, String password2, String telefono) throws MiException {
        validar(nombre, apellido, email, password, password2, telefono);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setNombreUsuario(nombre);
            usuario.setEmail(email);
            usuario.setPassword(password);

            usuarioRepositorio.save(usuario);
        } else {
            throw new MiException("No se encontro ningún usuario con ese ID");
        }
    }

    // nota queda por reseatear el sueldo cuando se da de baja o se modifica el rol a USER.
    public void modificarEstado(String id) throws MiException {
        if (id == null || id.isEmpty()) {
            throw new MiException("El id no puede ser nulo o estar vacio");
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            if (usuario.getActivo().equals(Boolean.TRUE)) {
                usuario.setActivo(Boolean.FALSE);
            } else {
                usuario.setActivo(Boolean.TRUE);
            }
            // POR ULTIMO GUARDO LOS CAMBIOS 
            usuarioRepositorio.save(usuario);

        } else {
            throw new MiException("No se encontro ningún usuario con ese ID");
        }
    }

    public List<Usuario> listarUsuarios() {

        // creacion de una lista que almacenara los usuarios con rol periodista
        List<Usuario> usuarios = new ArrayList();

        //la lista va a contener los usuarios que me devuelva el repositorio 
        //EN UsuarioRepositorio hay una Query que me devuelve los usuarios con el ROL de periodista
        usuarios = usuarioRepositorio.buscarUsuarios();

        // RETORTNO LA LISTA AL CONTROLADOR admin/listarPeridista para inyectarlos en una tabla
        return usuarios;
    }

//    public Usuario getOne(String id) {
//        return usuarioRepositorio.getById(id);
//    }
}
