package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Imagen;
import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.PropiedadRepositorio;
import com.egg.alquileres.repositorios.UsuarioRepositorio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PropiedadServicio {

    private final PropiedadRepositorio propiedadRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ImagenServicio imagenServicio;

    public PropiedadServicio(PropiedadRepositorio propiedadRepositorio, UsuarioRepositorio usuarioRepositorio, ImagenServicio imagenServicio) {
        this.propiedadRepositorio = propiedadRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.imagenServicio = imagenServicio;
    }

    private void validar(String nombre, String direccion, String ciudad, Double precio, Usuario propietario, MultipartFile fotos) throws MiException {
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
        if (fotos == null || fotos.isEmpty()) {
            throw new MiException("Debe ingresar una foto.");
        }
    }

    @Transactional
    public void crearPropiedad(String nombre, String direccion, String ciudad, Double precio, Usuario propietario, MultipartFile fotos) throws MiException, ParseException {

        validar(nombre, direccion, ciudad, precio, propietario, fotos);

        // Crear una lista para guardar las fechas disponibles
        Set<Date> fechasDisponibles = new TreeSet();

        // Obtener la fecha actual
        Calendar fechaActual = Calendar.getInstance();

        // Obtener el último día del año
        Calendar finDeAnio = Calendar.getInstance();
        finDeAnio.set(Calendar.MONTH, Calendar.DECEMBER);
        finDeAnio.set(Calendar.DAY_OF_MONTH, 31);

        // Agregar todas las fechas desde la fecha actual hasta el fin de año a la lista de fechas disponibles
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        while (fechaActual.before(finDeAnio)) {
            fechaActual.add(Calendar.DATE, 1);
            fechasDisponibles.add(sdf.parse(sdf.format(fechaActual.getTime())));
        }

        Imagen imagen = imagenServicio.crearImagen(fotos);

        // Retornar una nueva instancia de Casa con los parámetros proporcionados y las fechas disponible
        Propiedad propiedad = new Propiedad();

        propiedad.setNombre(nombre);
        propiedad.setDireccion(direccion);
        propiedad.setCiudad(ciudad);
        propiedad.setPrecio_base(precio);
        propiedad.setEstado(Boolean.TRUE);
        propiedad.setPropietario(propietario);
        propiedad.setFechasDisponibles((Set<Date>) fechasDisponibles);
        
        propiedad.setFotos(new HashSet());
        propiedad.getFotos().add(imagen);

        // Si es un admin el que crea la noticia la guardo sin idCreador la relacion es con periodista
        propiedadRepositorio.save(propiedad);
    }

    public void modificarPropiedad(String nombre, String direccion, String ciudad, Double precio, Usuario propietario, MultipartFile fotos){
        /* TO DO */
    }
    
    @Transactional
    public void modificarImagenPropiedad(String id, MultipartFile archivo) throws MiException {

        if (id == null || id.isEmpty()) {
            throw new MiException("El ID de la noticia no puede ser nulo ni estar vacio.");
        }

        Optional<Propiedad> respuesta = propiedadRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Propiedad propiedad = respuesta.get();

            //A desarrollar 
        } else {
            throw new MiException("No se encontro el ID de la noticia solicitado");
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

            usuarioRepositorio.save(propietario);
            propiedadRepositorio.deleteById(propiedad.getId());

        } else {
            throw new MiException("No existe una Noticia con ese ID");
        }
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

    @Transactional(readOnly = true)
    public Propiedad listarPropiedadesPorPropietario(String id) throws MiException {

        List<Propiedad> respuesta = propiedadRepositorio.buscarPorPropietario(id);

        return (Propiedad) respuesta;
    }

    public Propiedad getOne(String id) {
        return propiedadRepositorio.getById(id);
    }

    public List<Propiedad> buscarPropiedadPorPropietario(String idPropietario) {
        return propiedadRepositorio.buscarPorPropietario(idPropietario);
    }

    public List<Propiedad> listarPropiedades() {

        List<Propiedad> propiedades = new ArrayList();

        propiedades = propiedadRepositorio.findAll(Sort.by(Sort.Direction.ASC, "nombre"));

        return propiedades;
    }
}
