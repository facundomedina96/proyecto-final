package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Prestacion;
import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Reserva;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.enumeraciones.EstadoReserva;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.ReservaRepositorio;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaServicio {

    private final ReservaRepositorio reservaRepositorio;
    private final PropiedadServicio propiedadServicio;
    private final PrestacionServicio prestacionServicio;

    public ReservaServicio(ReservaRepositorio reservaRepositorio, PropiedadServicio propiedadServicio, PrestacionServicio prestacionServicio) {
        this.reservaRepositorio = reservaRepositorio;
        this.propiedadServicio = propiedadServicio;
        this.prestacionServicio = prestacionServicio;
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

        // Crear una lista de prestaciones en la reserva;
        List<Prestacion> prestacionesReserva = new ArrayList();

        // Validar los checkbox
        if (dj) {
            precioCalculado += obtenerCostoPrestacion("DJ", prestaciones);
            Prestacion prestacion = obtenerPrestacion("DJ", prestaciones);
            prestacionesReserva.add(prestacionServicio.crearPrestacion(prestacion.getNombre(), prestacion.getPrecio(), Boolean.TRUE));
        }

        if (catering) {
            precioCalculado += obtenerCostoPrestacion("CATERING", prestaciones);
            Prestacion prestacion = obtenerPrestacion("CATERING", prestaciones);
            prestacionesReserva.add(prestacionServicio.crearPrestacion(prestacion.getNombre(), prestacion.getPrecio(), Boolean.TRUE));
        }

        if (pileta) {
            precioCalculado += obtenerCostoPrestacion("PILETA", prestaciones);
            Prestacion prestacion = obtenerPrestacion("PILETA", prestaciones);
            prestacionesReserva.add(prestacionServicio.crearPrestacion(prestacion.getNombre(), prestacion.getPrecio(), Boolean.TRUE));
        }

        return crearYPersistirReserva(fechaDesde, fechaHasta, precioCalculado, propiedad, cliente, prestacionesReserva);
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

    private Prestacion obtenerPrestacion(String nombrePrestacion, List<Prestacion> prestaciones) {
        for (Prestacion prestacion : prestaciones) {
            if (prestacion.getNombre().equalsIgnoreCase(nombrePrestacion)) {
                return prestacion;
            }
        }
        return null;
    }

    private Reserva crearYPersistirReserva(Date fechaDesde, Date fechaHasta, Double precioCalculado, Propiedad propiedad, Usuario cliente, List<Prestacion> prestaciones) {
        Reserva reserva = new Reserva();
        reserva.setFechaDesde(fechaDesde);
        reserva.setFechaHasta(fechaHasta);
        reserva.setPrecio(precioCalculado);
        reserva.setPropiedad(propiedad);
        reserva.setCliente(cliente);
        if (prestaciones != null || !prestaciones.isEmpty()) {
            reserva.setPrestaciones(prestaciones);
        }
        reserva.setEstado(EstadoReserva.ACTIVA);
        reservaRepositorio.save(reserva);
        return reserva;
    }

    public List<Reserva> listarReservasDeUnUsuario(String idUsuario) throws MiException {

        List<Reserva> reservas = new ArrayList();
        reservas = reservaRepositorio.buscarPorCliente(idUsuario);

        // Actualizar el estado de las reservas del usuario
        for (Reserva reserva : reservas) {
            actualizarEstado(reserva);
        }
        return reservas;
    }
    
    public List<Reserva> listarReservasDeUnaPropiedad(String idPropiedad) throws MiException{
        List<Reserva> reservas = new ArrayList();
        reservas = reservaRepositorio.listarReservasDeUnaPropiedad(idPropiedad);

        return reservas;
    }

    public void eliminarReserva(String id) {
        reservaRepositorio.deleteById(id);
    }

    public void actualizarEstado(Reserva reserva) {
        Date fechaFinalizacion = reserva.getFechaHasta();
        Date fechaActual = obtenerFechaActual();

        if (fechaActual.after(fechaFinalizacion)) {
            reserva.setEstado(EstadoReserva.COMPLETADA);
            reservaRepositorio.save(reserva);
        }
    }
    
    public List<Reserva> listarReservas(){
        return reservaRepositorio.findAll();
    }
}
