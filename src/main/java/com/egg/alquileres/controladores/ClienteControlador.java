package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.UsuarioServicio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Hernan E Encizo
 */
@Controller
@PreAuthorize("hasAnyRole('ROLE_CLIENTE')")
@RequestMapping("/cliente")
public class ClienteControlador {

    private final UsuarioServicio usuarioServicio;

    public ClienteControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @PostMapping("/crearReserva/{propiedad_id}")
    public String crearReserva(ModelMap modelo, HttpSession session, @RequestParam("fechaInicio") String fechaInicio, @RequestParam("fechaFin") String fechaFin,
            @PathVariable String propiedad_id,
            @RequestParam(name = "PILETA", required = false) boolean PILETA,
            @RequestParam(name = "DJ", required = false) boolean DJ,
            @RequestParam(name = "CATERING", required = false) boolean CATERING) {

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
            
            Usuario cliente = (Usuario) session.getAttribute("usuarioSession");

            try {
                usuarioServicio.crearReserva(fechaDesde, fechaHasta, cliente, propiedad_id, DJ, CATERING, PILETA);
                modelo.put("exito", "Se ha creado la reserva con exito");
                modelo.put("usuario", cliente);
                
            } catch (MiException ex) {
                modelo.put("error", ex.getMessage());
            }
            return "panel.html";
        } catch (ParseException ex) {
            modelo.put("error", "Error con las fechas seleccionadas");
        }
        return "panel.html";
    }
}
