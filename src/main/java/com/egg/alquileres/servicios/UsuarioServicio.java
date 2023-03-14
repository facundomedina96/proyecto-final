package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.enumeraciones.Rol;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService {

<<<<<<< Updated upstream
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private PropietarioServicio propietarioServicio;

    // MËTODO PARA VALIDAR LOS PARAMETROS RECIBIDOS DEL FORMULARIO
    private void validar(String nombre, String apellido, String email, String password, String password2, String telefono) throws MiException {
=======
    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public void validar(String nombre, String apellido, String email, String password, String password2, String telefono) throws MiException {
>>>>>>> Stashed changes
        if (nombre == null || nombre.isEmpty()) {
            throw new MiException("El nombre no puede ser nulo ni estar vacio.");
        }

        if (apellido == null || nombre.isEmpty()) {
            throw new MiException("El apellido no puede ser nulo ni estar vacio.");
        }

        if (email == null || nombre.isEmpty()) {
            throw new MiException("El Email no puede ser nulo ni estar vacio.");
        }
        if (password == null || nombre.isEmpty() || password.length() <= 5) {
            throw new MiException("La contraseña no puede ser nulo ni estar vacio y debe contener mas de 5 dígitos.");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas deben ser iguales");
        }
        if (telefono == null || nombre.isEmpty()) {
            throw new MiException("El numero de telefono no puede ser nulo ni estar vacio.");
        }

    }

    @Transactional
<<<<<<< Updated upstream
    public void registrar(String nombre, String apellido, String email, String password, String password2, String telefono, String rol) throws MiException {
        // llamo al metodo validar pasando los parametros recibidos del form
        validar(nombre, apellido, email, password, password2, telefono);

        // Antes de persistir pregunto que rol tendra y llamo al correspondiente servicio
        if (rol.equalsIgnoreCase("cliente")) {
            clienteServicio.registrarCliente(nombre, apellido, email, password, password2, telefono);
        }else{
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
=======
    public void registrar(String nombre, String apellido, String email, String password, String password2, String telefono, Rol rol) throws MiException {

        validar(nombre, apellido, email, password, password2, telefono);

        if (rol == null) {
            throw new MiException("El rol no puede ser nulo.");
        }
>>>>>>> Stashed changes

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);

        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setTelefono(telefono);
        usuario.setActivo(Boolean.TRUE);
        usuario.setRol(rol);
        
        usuarioRepositorio.save(usuario);
    }

    public Usuario getOne(String id) {
        return usuarioRepositorio.getById(id);
    }

    public void modificar(String id, String nombre, String apellido, String email, String password, String password2, String telefono) throws MiException {

        validar(nombre, apellido, email, password, password2, telefono);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setPassword(new BCryptPasswordEncoder().encode(password));
            usuario.setTelefono(telefono);

            usuarioRepositorio.save(usuario);
        } else {
            throw new MiException("No se encontro ningún usuario con ese ID");
        }
    }

    public void eliminar(String id) throws MiException {

        if (id == null || id.isEmpty()) {
            throw new MiException("El id no puede ser nulo o estar vacio");
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            respuesta.get().setActivo(Boolean.FALSE);
        } else {
            throw new MiException("No se encontro ningún usuario con ese ID");
        }
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList();
        usuarios = usuarioRepositorio.buscarUsuarios();
        return usuarios;
    }

<<<<<<< Updated upstream
    public Usuario getOne(String id) {
        return usuarioRepositorio.getById(id);
=======
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority per = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            permisos.add(per);

            User user = new User(usuario.getEmail(), usuario.getPassword(), permisos);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuarioSession", usuario);

            return user;
        } else {
            return null;
        }
>>>>>>> Stashed changes
    }
}
