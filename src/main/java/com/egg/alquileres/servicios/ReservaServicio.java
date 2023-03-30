package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Prestacion;
import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Reserva;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.ReservaRepositorio;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReservaServicio {

    private final ReservaRepositorio reservaRepositorio;
    private final PropiedadServicio propiedadServicio;

    public ReservaServicio(ReservaRepositorio reservaRepositorio, PropiedadServicio propiedadServicio) {
        this.reservaRepositorio = reservaRepositorio;
        this.propiedadServicio = propiedadServicio;
    }

    public Reserva crearReserva(Date fechaDesde, Date fechaHasta, Usuario cliente, String idPropiedad,
            boolean dj, boolean catering, boolean pileta) throws MiException {

        // Buscar la propiedad
        Propiedad propiedad = propiedadServicio.buscarPropiedadPorId(idPropiedad);

        //Validar las fechas dentro del rango
        validarFechasDisponibles(fechaDesde, fechaHasta, propiedad);

        // Validar si las fechas ya no estan reservadas
        validarReservasActivas(fechaDesde, fechaHasta, propiedad);

        // Calcular el precio de los dias de estadía
        Double precioCalculado = calcularPrecioEstadia(fechaDesde, fechaHasta, propiedad);

        // Obtener las prestaciones de la propiedad
        List<Prestacion> prestaciones = propiedad.getPrestaciones();

        // Validar los checkbox
        if (dj) {
            precioCalculado += obtenerCostoPrestacion("DJ", prestaciones);
        }

        if (catering) {
            precioCalculado += obtenerCostoPrestacion("CATERING", prestaciones);
        }

        if (pileta) {
            precioCalculado += obtenerCostoPrestacion("PILETA", prestaciones);
        }

        return crearYPersistirReserva(fechaDesde, fechaHasta, precioCalculado, propiedad, cliente);
    }

    private void validarFechasDisponibles(Date fechaDesde, Date fechaHasta, Propiedad propiedad) throws MiException {
        Date fechaActual = obtenerFechaActual();
        Date fechaFinAnio = propiedad.getFechaFinAnio();
        

        // Validar que fechaDesde sea menor a fechaHasta
        if (fechaDesde.after(fechaHasta)) {
            throw new MiException("La fecha de inicio de la reserva debe ser anterior a la fecha de final");
        }

        // Validar que fechaDesde sea posterior a fechaCreacion
        if (fechaDesde.before(fechaActual)) {
            throw new MiException("La fecha de inicio no puede ser anterior a la fecha actual");
        }

        // Validar que fechaHasta sea igual o menor a fechaFinAnio
        if (fechaHasta.after(fechaFinAnio)) {
            throw new MiException("Lo sentimos. La fecha final de la reserva no puede ser posterior al final del año");
        }
    }

    private Date obtenerFechaActual() {
        // Obtener la fecha actual
        Date fechaActual = new Date();

        // Crear un objeto de tipo Calendar
        Calendar cal = Calendar.getInstance();

        // Establecer la fecha actual en el objeto Calendar
        cal.setTime(fechaActual);

        // Establecer la hora, minutos y segundos a cero
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Obtener la fecha actual sin la hora, minutos y segundos
        Date fechaSinHora = cal.getTime();
        return fechaSinHora;
    }

    private void validarReservasActivas(Date fechaDesde, Date fechaHasta, Propiedad propiedad) throws MiException {
        List<Reserva> reservasActivas = propiedad.getReservasActivas();
        for (Reserva reserva : reservasActivas) {
            if (fechaDesde.before(reserva.getFechaHasta()) && reserva.getFechaDesde().before(fechaHasta)) {
                throw new MiException("Lo sentimos. Ya existe una reserva activa en esas fechas");
            }
        }
    }

    private Double calcularPrecioEstadia(Date fechaDesde, Date fechaHasta, Propiedad propiedad) {
        int diasEstadia = (int) ChronoUnit.DAYS.between(fechaDesde.toInstant(), fechaHasta.toInstant());
        Double precioCalculado = propiedad.getPrecio_base() * diasEstadia;
        return precioCalculado;
    }

    private Double obtenerCostoPrestacion(String nombrePrestacion, List<Prestacion> prestaciones) {
        for (Prestacion prestacion : prestaciones) {
            if (prestacion.getNombre().equalsIgnoreCase(nombrePrestacion)) {
                return prestacion.getPrecio();
            }
        }
        return 0.0;
    }

    private Reserva crearYPersistirReserva(Date fechaDesde, Date fechaHasta, Double precioCalculado, Propiedad propiedad, Usuario cliente) {
        Reserva reserva = new Reserva();
        reserva.setFechaDesde(fechaDesde);
        reserva.setFechaHasta(fechaHasta);
        reserva.setPrecio(precioCalculado);
        reserva.setPropiedad(propiedad);
        reserva.setCliente(cliente);
        reservaRepositorio.save(reserva);
        return reserva;
    }
}
