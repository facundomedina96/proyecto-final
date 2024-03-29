package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Imagen;
import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Reserva;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.enumeraciones.Rol;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.ReservaRepositorio;
import com.egg.alquileres.repositorios.UsuarioRepositorio;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final ImagenServicio imagenServicio;
    private final ReservaRepositorio reservaRepositorio;
    private final PropiedadServicio propiedadServicio;
    private final ReservaServicio reservaServicio;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio, ImagenServicio imagenServicio, ReservaRepositorio reservaRepositorio, PropiedadServicio propiedadServicio, ReservaServicio reservaServicio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.imagenServicio = imagenServicio;
        this.reservaRepositorio = reservaRepositorio;
        this.propiedadServicio = propiedadServicio;
        this.reservaServicio = reservaServicio;
    }

    public void validar(String nombre, String apellido, String email, String password, String password2,
            String telefono) throws MiException {
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

    public void registrar(String nombre, String apellido, String email, String password, String password2,
            String telefono, Rol rol, MultipartFile foto_perfil) throws MiException {

        validar(nombre, apellido, email, password, password2, telefono);

        if (rol == null) {
            throw new MiException("El rol no puede ser nulo.");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);

        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setTelefono(telefono);
        usuario.setActivo(Boolean.TRUE);
        usuario.setRol(rol);

        if (foto_perfil == null || foto_perfil.isEmpty()) {
            usuario.setFoto_perfil(null);
        } else {
            Imagen imagen = imagenServicio.crearImagen(foto_perfil);
            usuario.setFoto_perfil(imagen);
        }
        usuarioRepositorio.save(usuario);
    }

    public Usuario getOne(String id) {
        return usuarioRepositorio.getById(id);
    }

    @Transactional
    public void modificar(String id, String nombre, String apellido, String email, String password, String password2,
            String telefono, MultipartFile foto_perfil) throws MiException {

        validar(nombre, apellido, email, password, password2, telefono);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setPassword(new BCryptPasswordEncoder().encode(password));
            usuario.setTelefono(telefono);

            // se crea la imagen para luego setearla al usuario
            if (foto_perfil == null || foto_perfil.isEmpty()) {
                usuario.setFoto_perfil(null);
            } else {
                Imagen imagen = imagenServicio.crearImagen(foto_perfil);
                usuario.setFoto_perfil(imagen);
            }
            usuarioRepositorio.save(usuario);
        } else {
            throw new MiException("No se encontro ningún usuario con ese ID");
        }
    }

    /*
     * Metodo eliminar(usuario) si bien cambia el estado del Usuario(Propietario o
     * Cliente) a FALSE;
     * El usuario todavia tiene capacidad para ingresar al sitio;
     * Soluciones: O lo eliminamos directamente de la BBDD para que ya no exista o
     * tenemos que agregar
     * condiciones en el inicio de sesion; para evitar que ingresen los usuarios con
     * estado activo.FALSE;
     * 
     * Tambien falta desarrollar mas el metodo ya que tanto Cliente como Propietario
     * (Y Admin)
     * apuntaran al mismo metodo para eliminar su perfil(O en el caso del admin el
     * perfil de alguien);
     * 
     * Aqui otro inconvenniente; Un cliente deberia darse de baja y quedar sin
     * acceso a la plataforma
     * sin mas, pero un Propetario al darse de baja, sus propiedades y todo lo
     * relacionado a el tambien
     * deberian hacerlo(es decir sus propiedades ya no deberian estar disponiblies,
     * ni las reservas);
     * 
     * Soluciones: Podemos agregar mas metodos eliminar, es decir seguiremos usando
     * eliminar
     * pero a la hora de buscar el usuario preguntaremos por el rol que tiene segun
     * su rol
     * redireccionar al metodo eliminarPropietario o eliminarCliente que
     * desarrolaran la logica
     * adecuada para cada caso; La otra solucion seria desarrollar todo el codigo
     * con validaciones
     * dentro del metodo eliminar;
     */
    @Transactional
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

    public Usuario buscarPorEmail(String email) {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
        return usuario;
    }

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
    }

 @Transactional
    public Reserva crearReserva(Date fechaDesde, Date fechaHasta, Usuario cliente, String id_propiedad, boolean dj, boolean catering, boolean pileta) throws MiException, ParseException {

        Reserva reserva = reservaServicio.crearReserva(fechaDesde, fechaHasta, cliente, id_propiedad, dj, catering, pileta);
        propiedadServicio.actualizarYGuardarReservas(reserva, id_propiedad);
        return reserva;
    }

    @Transactional
    public void eliminarReserva(String id) throws MiException {
        Optional<Reserva> respuesta = reservaRepositorio.findById(id);

        if (respuesta.isPresent()) {
            // si la respuesta esta presente buscar la reserva en la propiedad y eliminarla
            Reserva reserva = respuesta.get();
            
            Propiedad propiedad = reserva.getPropiedad();
            
            propiedadServicio.eliminarReserva(propiedad, id);
            reservaServicio.eliminarReserva(id);
            
        } else {
            throw new MiException("No existe una reserva con ese ID");
        }
    }

    // Metodo agregado para que los Usuarios-Propietarios puedan ver sus propiedades
    public List<Propiedad> listarPropiedades(String idPropietario) throws MiException {

        List<Propiedad> propiedades = new ArrayList();

        propiedades = propiedadServicio.listarPropiedadesPorPropietario(idPropietario);
        return propiedades;
    }

    public List<Usuario> buscarPropietarios() {
        return usuarioRepositorio.buscarPropietarios(Rol.PROPIETARIO);
    }
    
    public List<Usuario> buscarPropietariosActivos() {
        return usuarioRepositorio.buscarPropietariosActivos(Rol.PROPIETARIO);
    }
    
    //Metodo para que listar las reservas de un Cliente
    public List<Reserva> listarReservasDeUnUsuario(String idUsuario) throws MiException {
        return reservaServicio.listarReservasDeUnUsuario(idUsuario);
    }
}
