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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
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

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio, ImagenServicio imagenServicio, ReservaRepositorio reservaRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.imagenServicio = imagenServicio;
        this.reservaRepositorio = reservaRepositorio;
    }

    public void validar(String nombre, String apellido, String email, String password, String password2, String telefono, MultipartFile foto_perfil) throws MiException {
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

        //En vez de validar la foto de perfil, se le podria asignar una foto de perfil base
        if (foto_perfil == null || foto_perfil.isEmpty()) {
            throw new MiException("Error: No se ha logrado cargar la foto de perfil.");
        }
    }

    public void registrar(String nombre, String apellido, String email, String password, String password2, String telefono, Rol rol, MultipartFile foto_perfil) throws MiException {

        validar(nombre, apellido, email, password, password2, telefono, foto_perfil);

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

        Imagen imagen = imagenServicio.crearImagen(foto_perfil);

        usuario.setFoto_perfil(imagen);

        usuarioRepositorio.save(usuario);
    }

    public Usuario getOne(String id) {
        return usuarioRepositorio.getById(id);
    }

    public void modificar(String id, String nombre, String apellido, String email, String password, String password2, String telefono, MultipartFile foto_perfil) throws MiException {

        validar(nombre, apellido, email, password, password2, telefono, foto_perfil);

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
    public void crearReserva(String id, String nombre, String apellido, String email, String password, String password2, String telefono, Date Date, Usuario cliente, Date fechaDesde, Propiedad propiedad, Date fechaHasta, MultipartFile foto_perfil) throws MiException, ParseException {

        validar(nombre, apellido, email, password, password2, telefono, foto_perfil);

        Set<Date> fechasDisponibles = new TreeSet();

        Calendar fechaActual = Calendar.getInstance();

        Calendar finDeAnio = Calendar.getInstance();
        finDeAnio.set(Calendar.MONTH, Calendar.DECEMBER);
        finDeAnio.set(Calendar.DAY_OF_MONTH, 31);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        while (fechaActual.before(finDeAnio)) {
            fechaActual.add(Calendar.DATE, 1);
            fechasDisponibles.add(sdf.parse(sdf.format(fechaActual.getTime())));
        }

        Reserva reserva = new Reserva();

        reserva.setCliente(cliente);
        reserva.setFechaDesde(fechaDesde);
        reserva.setFechaHasta(fechaHasta);
        reserva.setId(id);
        reserva.setPrecio(Double.NaN);
        reserva.setPropiedad(propiedad);

        reservaRepositorio.save(reserva);

    }

    @Transactional
    public void eliminarReserva(String id) throws MiException {
        Optional<Reserva> respuesta = reservaRepositorio.findById(id);

        if (respuesta.isPresent()) {

            Reserva reserva = respuesta.get();

            Usuario cliente = new Usuario();

            cliente = reserva.getCliente();


            List<Reserva> reservas = reservaRepositorio.buscarPorCliente(cliente.getId());

            Iterator<Reserva> it = reservas.iterator();

            while (it.hasNext()) {
                Reserva aux = it.next();
                if (aux.getId().equals(id)) {
                    it.remove();
                    break;
                }
            }

            reservaRepositorio.save(reserva);

            reservaRepositorio.deleteById(reserva.getId());

        } else {
            throw new MiException("No existe una reserva con ese ID");
        }

    }
}
