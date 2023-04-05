package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Imagen;
import com.egg.alquileres.entidades.Prestacion;
import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Reserva;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.PropiedadRepositorio;
import com.egg.alquileres.repositorios.UsuarioRepositorio;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PropiedadServicio {

    private final PropiedadRepositorio propiedadRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ImagenServicio imagenServicio;
    private final PrestacionServicio prestacionServicio;

    public PropiedadServicio(PropiedadRepositorio propiedadRepositorio, UsuarioRepositorio usuarioRepositorio,
            ImagenServicio imagenServicio, PrestacionServicio prestacionServicio) {
        this.propiedadRepositorio = propiedadRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.imagenServicio = imagenServicio;
        this.prestacionServicio = prestacionServicio;
    }

    private void validar(String nombre, String direccion, String ciudad, Double precio, Usuario propietario, MultipartFile[] fotos) throws MiException {

        if (nombre == null || nombre.isEmpty()) {
            throw new MiException("El nombre no puede ser nulo ni estar vacio.");
        }
        if (direccion == null || direccion.isEmpty()) {
            throw new MiException("La direccion no puede ser nulo ni estar vacio.");
        }
        if (ciudad == null || ciudad.isEmpty()) {
            throw new MiException("La ciudad no puede ser nulo ni estar vacio.");
        }
        if (precio == null) {
            throw new MiException("El precio no puede ser nulo ni estar vacio.");
        }
        if (propietario == null) {
            throw new MiException("El propietario no puede ser nulo ni estar vacio.");
        }
        if (fotos == null) {
            throw new MiException("Debe ingresar una foto.");
        }
    }

    @Transactional
    public void crearPropiedad(String nombre, String direccion, String ciudad,
            Double precio, Usuario propietario, MultipartFile[] fotos,
            String nombreD, Double precioD, Boolean activoD,
            String nombreC, Double precioC, Boolean activoC,
            String nombreP, Double precioP, Boolean activoP) throws MiException, ParseException {

        validar(nombre, direccion, ciudad, precio, propietario, fotos);

        // Obtener la fecha actual
        LocalDate fecha = LocalDate.now();
        Date fechaActual = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Obtener ultimo dia del año;
        LocalDate ultimoDiaDelAnio = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        Date fechaFinAnio = Date.from(ultimoDiaDelAnio.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Retornar una nueva instancia de Casa con los parámetros proporcionados y las
        // fechas disponible
        Propiedad propiedad = new Propiedad();

        propiedad.setNombre(nombre);
        propiedad.setDireccion(direccion);
        propiedad.setCiudad(ciudad);
        propiedad.setPrecio_base(precio);
        propiedad.setEstado(Boolean.TRUE);
        propiedad.setPropietario(propietario);

        propiedad.setFechaCreacion(fechaActual);
        propiedad.setFechaFinAnio(fechaFinAnio);

        propiedad.setFotos(new HashSet<>());

        for (MultipartFile foto : fotos) {
            propiedad.getFotos().add(imagenServicio.crearImagen(foto));
        }

        // Falta validar que los valores de las prestaciones no venga nulos 
        // si no se persisten servicios vacios. 
        Prestacion prestacion1 = crearPrestacion(nombreD, precioD, activoD);
        Prestacion prestacion2 = crearPrestacion(nombreC, precioC, activoC);
        Prestacion prestacion3 = crearPrestacion(nombreP, precioP, activoP);

        if (prestacion1 != null || prestacion2 != null || prestacion2 != null) {
            List<Prestacion> prestaciones = new ArrayList();
            propiedad.setPrestaciones(agregarPrestacionesAPropiedad(prestaciones, prestacion1, prestacion2, prestacion3));
        }

        propiedadRepositorio.save(propiedad);
        System.out.println("Propiedad persistida");
    }

    private Prestacion crearPrestacion(String nombre, Double precio, Boolean activo) {
        if (activo != null && precio != null) {
            return prestacionServicio.crearPrestacion(nombre, precio, activo);
        }
        return null;
    }

    public List<Prestacion> agregarPrestacionesAPropiedad(List<Prestacion> prestaciones,
            Prestacion prestacion1, Prestacion prestacion2, Prestacion prestacion3) {

        if (prestacion1 != null) {
            prestaciones.add(prestacion1);
        }
        if (prestacion2 != null) {
            prestaciones.add(prestacion2);
        }
        if (prestacion3 != null) {
            prestaciones.add(prestacion3);
        }
        return prestaciones;
    }

    public Propiedad getOne(String id) {
        return propiedadRepositorio.getById(id);
    }

    @Transactional(readOnly = true)
    public Propiedad buscarPropiedadPorId(String id) throws MiException {
        Optional<Propiedad> respuesta = propiedadRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Propiedad propiedad = respuesta.get();
            return propiedad;
        } else {
            throw new MiException("No existe una Propiedad con ese ID");
        }
    }

    public List<Propiedad> buscarPropiedadPorPropietario(String idPropietario) {
        return propiedadRepositorio.buscarPorPropietario(idPropietario);
    }

    public List<Propiedad> buscarPorPropietariosActivos() {
        return propiedadRepositorio.buscarPorPropietariosActivos();
    }

    public List<Propiedad> buscarPorCiudad(String ciudad) {
        return propiedadRepositorio.buscarPorCiudad(ciudad);
    }

    public List<Propiedad> buscarPorPropietarioYCiudad(String idPropietario, String ciudad) {
        return propiedadRepositorio.buscarPorPropietarioYCiudad(idPropietario, ciudad);
    }

    public List<Propiedad> buscarPorPrecioMax(Double precioMax) {
        return propiedadRepositorio.buscarPorPrecioMax(precioMax);
    }

    public List<Propiedad> buscarPorPropietarioCiudadPrecio(String idPropietario, String ciudad, Double precio) {
        return propiedadRepositorio.buscarPorPropietarioCiudadPrecio(idPropietario, ciudad, precio);
    }

    public List<Propiedad> buscarPorPropietarioYPrecio(String idPropietario, Double precio) {
        return propiedadRepositorio.buscarPorPropietarioYPrecio(idPropietario, precio);
    }

    public List<Propiedad> buscarPorCiudadYPrecio(String ciudad, Double precio) {
        return propiedadRepositorio.buscarPorCiudadYPrecio(ciudad, precio);
    }

    public List<Propiedad> listarPropiedades() {

        List<Propiedad> propiedades = new ArrayList();

        propiedades = propiedadRepositorio.findAll(Sort.by(Sort.Direction.ASC, "nombre"));

        return propiedades;
    }

    // modifique este metodo estaba mal devolvia una sola propiedad en vez de una
    // lista
    @Transactional(readOnly = true)
    public List<Propiedad> listarPropiedadesPorPropietario(String id) throws MiException {

        List<Propiedad> propiedades = new ArrayList();

        propiedades = propiedadRepositorio.buscarPorPropietario(id);

        return propiedades;
    }

    @Transactional
    public void modificarImagenPropiedad(String id, MultipartFile archivo) throws MiException {

        if (id == null || id.isEmpty()) {
            throw new MiException("El ID de la entidad no puede ser nula ni estar vacía.");
        }

        Optional<Propiedad> respuesta = propiedadRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Propiedad propiedad = respuesta.get();

            // A desarrollar
        } else {
            throw new MiException("No se encontro el ID de la entidad solicitada.");
        }
    }

    @Transactional
    public void eliminarPropiedad(String id) throws MiException {
        Optional<Propiedad> respuesta = propiedadRepositorio.findById(id);

        if (respuesta.isPresent()) {

            Propiedad propiedad = respuesta.get();

            Usuario propietario = new Usuario();

            propietario = propiedad.getPropietario();

            List<Propiedad> propiedades = propiedadRepositorio.buscarPorPropietario(propietario.getId());

            Iterator<Propiedad> it = propiedades.iterator();

            while (it.hasNext()) {
                Propiedad aux = it.next();
                if (aux.getId().equals(id)) {
                    it.remove();
                    break;
                }
            }

            for (Imagen img : propiedad.getFotos()) {
                imagenServicio.eliminarImagen(img.getId());
            }

            usuarioRepositorio.save(propietario);
            propiedadRepositorio.deleteById(propiedad.getId());

        } else {
            throw new MiException("No existe una Entidad con ese ID");
        }
    }

    /*
     * Por ahora el propietario solo puede modificar estos atributos, Si se opta por
     * darle la opcion
     * de modificar mas atributos, Modificar el HTML para pedir los datos, el
     * controlador, y por ultimo este servicio.
     */
    public void modificarPropiedad(String id, String nombre, String direccion, String ciudad, Double precio, MultipartFile fotos) throws MiException {

        // Buscar la propiedad en la BBDD y la guardamos en respuesta
        Optional<Propiedad> respuesta = propiedadRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Propiedad propiedad = respuesta.get();
            propiedad.setNombre(nombre);
            propiedad.setDireccion(direccion);
            propiedad.setCiudad(ciudad);
            propiedad.setPrecio_base(precio);

            Imagen imagen = imagenServicio.crearImagen(fotos);

            Set<Imagen> imagenes = propiedad.getFotos();
            imagenes.add(imagen);

            propiedad.setFotos(imagenes);

            propiedadRepositorio.save(propiedad);
        } else {
            throw new MiException("No se encontro ningúna Propiedad con ese ID");
        }
    }

    public void actualizarYGuardarReservas(Reserva reserva, String id_propiedad) throws MiException {
        Propiedad propiedad = buscarPropiedadPorId(id_propiedad);
        List<Reserva> reservas = propiedad.getReservasActivas();
        reservas.add(reserva);
        propiedad.setReservasActivas(reservas);
        propiedadRepositorio.save(propiedad);
    }

    @Transactional
    public void eliminarReserva(Propiedad propiedad, String idReserva) {
        List<Reserva> reservasActivas = propiedad.getReservasActivas();

        for (Reserva reserva : reservasActivas) {
            if (reserva.getId().equals(idReserva)) {
                reservasActivas.remove(reserva);
                propiedad.setReservasActivas(reservasActivas);
                propiedadRepositorio.save(propiedad);
                return;
            }
        }
        throw new IllegalArgumentException("No se encontró la reserva con id " + idReserva);
    }
}
