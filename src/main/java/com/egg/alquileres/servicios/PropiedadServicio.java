package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Propietario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.PropiedadRepositorio;
import com.egg.alquileres.repositorios.PropietarioRepositorio;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Hernan E Encizo
 */
@Service
public class PropiedadServicio {

    @Autowired
    private PropiedadRepositorio propiedadRepositorio;
    @Autowired
    private PropietarioRepositorio propietarioRepositorio;

    private void validar(String nombre, String direccion, String ciudad, Double precio, Propietario propietario) throws MiException {
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
    }

    @Transactional
    public void crearPropiedad(String nombre, String direccion, String ciudad, Double precio, Propietario propietario) throws MiException, ParseException {

        
        
        validar(nombre, direccion, ciudad, precio, propietario);
        
        // Crear una lista para guardar las fechas disponibles
        Set<Date> fechasDisponibles = new TreeSet<>();
        System.out.println("fechas disponibles paso.");
        
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

        // Retornar una nueva instancia de Casa con los parámetros proporcionados y las fechas disponible
        Propiedad propiedad = new Propiedad();

        System.out.println("Proximo paso setear los valores");
        propiedad.setNombre(nombre);
        propiedad.setDireccion(direccion);
        propiedad.setCiudad(ciudad);
        propiedad.setPrecio_base(precio);
        propiedad.setEstado(Boolean.TRUE);
        propiedad.setPropietario(propietario);
        System.out.println("Seteo del propietario");
        propiedad.setFechasDisponibles((Set<Date>) fechasDisponibles);
        System.out.println("Seteo de las fechas disponibles se paso");
        
        // Si es un admin el que crea la noticia la guardo sin idCreador la relacion es con periodista
        propiedadRepositorio.save(propiedad);
        System.out.println("Propiedad persistida");
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

    //Nota: agregue validaciones para que se pueda modificar solo las noticias que le pertenecen a cada periodista
    // dicha validacion en el controlador podria hacer un metodo en el servicio que se encargue de dicha tarea.
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

            Propietario propietario = new Propietario();

            propietario = propiedad.getPropietario();

            // para poder eliminar una propiedad primeramente debo eliminar la relacion que existe con el propietario
            // es decir eliminar la FK de la tabla lista noticias.
            List<Propiedad> propiedades = propiedadRepositorio.buscarPorPropietario(propietario.getUsuario_id());

            Iterator<Propiedad> it = propiedades.iterator();

            while (it.hasNext()) {
                Propiedad aux = it.next();
                if (aux.getIdPropiedad().equals(id)) {
                    it.remove();
                    break;
                }
            }
            // en el bucle elimino la propiedad de la lista seteo la lista actualizada al id del propietario.
            propietario.setPropiedades((Set<Propiedad>) propiedades);

            propietarioRepositorio.save(propietario);

            // <<ELIMINACION DE LA NOTICIA DE LA BASE DE DATOS>>
            propiedadRepositorio.deleteById(propiedad.getIdPropiedad());

        } else {
            throw new MiException("No existe una Noticia con ese ID");
        }
    }

    public List<Propiedad> listarPropiedades() {

        List<Propiedad> propiedades = new ArrayList();

        propiedades = propiedadRepositorio.findAll(Sort.by(Sort.Direction.ASC, "nombre"));

        return propiedades;
    }

    public Propiedad getOne(String id) {
        return propiedadRepositorio.getById(id);
    }
}
