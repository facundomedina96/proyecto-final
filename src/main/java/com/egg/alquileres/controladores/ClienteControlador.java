package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.entidades.Reserva;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.UsuarioServicio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Hernan E Encizo
 */
@Controller
@PreAuthorize("hasAnyRole('ROLE_CLIENTE')")
@RequestMapping("/cliente")
public class ClienteControlador {

    private final UsuarioServicio usuarioServicio;
    private final PropiedadServicio propiedadServicio;

    public ClienteControlador(UsuarioServicio usuarioServicio, PropiedadServicio propiedadServicio) {
        this.usuarioServicio = usuarioServicio;
        this.propiedadServicio = propiedadServicio;
    }

    @PostMapping("/crearReserva/{propiedad_id}")
    public String crearReserva(ModelMap modelo, HttpSession session, @RequestParam("fechaInicio") String fechaInicio, @RequestParam("fechaFin") String fechaFin,
            @PathVariable String propiedad_id,
            @RequestParam(name = "PILETA", required = false) boolean PILETA,
            @RequestParam(name = "DJ", required = false) boolean DJ,
            @RequestParam(name = "CATERING", required = false) boolean CATERING) {

        Usuario cliente = (Usuario) session.getAttribute("usuarioSession");

        try {
            //Convertir la fecha String en tipo Date;             
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaDesde = formato.parse(fechaInicio);
            Date fechaHasta = formato.parse(fechaFin);

            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaDesde);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            fechaDesde = cal.getTime();

            cal.setTime(fechaHasta);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            fechaHasta = cal.getTime();
            // fin de la conversion de las fechas a Date

            try {
                Reserva reservas = usuarioServicio.crearReserva(fechaDesde, fechaHasta, cliente, propiedad_id, DJ, CATERING, PILETA);
                modelo.put("exito", "Se ha creado la reserva con exito");
                modelo.put("usuario", cliente);
                modelo.put("reservas", reservas);
                return "panel.html";

            } catch (MiException ex) {
                modelo.put("error", ex.getMessage());
                modelo.put("usuario", cliente);
            }

        } catch (ParseException ex) {
            modelo.put("error", "Error con las fechas seleccionadas");
            modelo.put("usuario", cliente);
        }
        return "panel.html";
    }

    // Metodo para que los clientes puedan listar sus reservas
    @GetMapping("/listarReservas")
    public String listarReservas(ModelMap model, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
        try {
            List<Reserva> reservas = usuarioServicio.listarReservasDeUnUsuario(usuario.getId());

            model.put("reservas", reservas);
            return "tabla_reserva.html";

        } catch (MiException ex) {
            model.put("error", ex.getMessage());
            model.put("usuario", usuario);
            return "panel.html";
        }
    }

    // Metodo para que el cliente elimine una reserva.
    @GetMapping("/eliminarReserva/{id}")
    public String eliminarReserva(ModelMap modelo, @PathVariable String id, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
        try {
            usuarioServicio.eliminarReserva(id);
            modelo.put("exito", "Se ha cancelado la reserva con exito");

        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
        }
        modelo.put("usuario", usuario);
        return "panel.html";
    }

    // COMENTARIOS DEL CLIENTE
    @GetMapping("/comentario/{id}")
    public String comentario(@PathVariable String id, ModelMap model) {

        Propiedad propiedad = propiedadServicio.getOne(id);
        model.put("propiedad", propiedad);
        return "comentario.html";
    }

    @PostMapping("/comentario/{id}")
    public String comentario(@PathVariable String id, ModelMap model, @RequestParam int calificacion, @RequestParam String opinion, HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
      
        try {
            
            Propiedad propiedad = propiedadServicio.buscarPropiedadPorId(id);
            
            System.out.println("Busqueda de la propiedad");
            usuarioServicio.crearComentario(propiedad, calificacion, opinion);

            model.put("exito", "Se agrego exitosamente su comentario");
            model.put("usuario", usuario);

        } catch (MiException ex) {
            model.put("error", ex.getMessage());
            model.put("usuario", usuario);

        }
        return "panel.html";
    }
}
