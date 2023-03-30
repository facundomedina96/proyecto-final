package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Comentario;
import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Reserva;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.ReservaRepositorio;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservaServicio {


    @Autowired
    private ReservaRepositorio reservaRepositorio;

    @Autowired
    private PropiedadServicio propiedadServicio;
    @Autowired
    private ComentarioServicio comentarioServicio;

    public Reserva crearReserva(Date fechaDesde, Date fechaHasta, Usuario cliente, String idPropiedad, Comentario opinion,int calificacion) throws MiException {
        
        // El precio no lo necesitaria si recibiria el objeto propiedad y podria consultar todo
        // lo que quisiera.
        
        // Obtener la instancia de la propiedad correspondiente al idPropiedad
        Propiedad propiedad = propiedadServicio.buscarPropiedadPorId(idPropiedad);

        // Obtener las fechas de creación y fin de año de la propiedad desde el Set de fechas disponibles
        Set<Date> fechasDisponibles = propiedad.getFechasDisponibles();

        Iterator<Date> it = fechasDisponibles.iterator();
        Date fechaCreacion = it.next();
        Date fechaFinAnio = it.next();
        
        

        // Verificar si las fechas de la reserva están dentro del rango de fechas disponibles de la propiedad
        if (fechaDesde.before(fechaCreacion) || fechaHasta.after(fechaFinAnio)) {
            throw new MiException("Las fechas seleccionadas no están disponibles");
        }

        // Verificar si las fechas de la reserva están disponibles (no hay reservas activas en esas fechas)
        List<Reserva> reservasActivas = propiedad.getReservasActivas();
        for (Reserva reserva : reservasActivas) {
            if (fechaDesde.before(reserva.getFechaHasta()) && reserva.getFechaDesde().before(fechaHasta)) {
                throw new MiException("Lo sentimos. Ya existe una reserva activa en esas fechas");
            }
        }

        // Calcular el precio de la reserva
        int diasEstadia = (int) ChronoUnit.DAYS.between(fechaDesde.toInstant(), fechaHasta.toInstant());
        Double precioCalculado = propiedad.getPrecio_base() * diasEstadia;

        // Crear la reserva y agregarla a la lista de reservas activas de la propiedad
        Reserva reserva = new Reserva();
        reserva.setFechaDesde(fechaDesde);
        reserva.setFechaHasta(fechaHasta);
        reserva.setPrecio(precioCalculado);
        reserva.setPropiedad(propiedad);
        reserva.setCliente(cliente);
        
        Comentario comentario = comentarioServicio.crearComentario(opinion);
        
        reserva.setOpinion((new ArrayList()));
        reserva.getOpinion().add(opinion);
        
        //Una vez que esta todo seteado procedo a llamar al repositorio
        reservaRepositorio.save(reserva);
        
        // Tanto agregar la reserva creada a la lista de reservasActivas 
        // le corresponderia al servicioPropiedad junto con persistir la propiedad con 
        // los cambios.
        return reserva;
    }
}
    
    