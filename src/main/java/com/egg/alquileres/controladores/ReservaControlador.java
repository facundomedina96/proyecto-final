/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.egg.alquileres.controladores;
import com.egg.alquileres.servicios.ReservaServicio;
import com.egg.alquileres.entidades.Prestacion;
import com.egg.alquileres.entidades.Propiedad;
import com.egg.alquileres.entidades.Reserva;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.ReservaServicio;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author sofia
 */
@Controller
public class ReservaControlador {
    @Autowired
    PropiedadServicio propiedadServicio;
    @Autowired
    ReservaServicio reservaServicio;
    @PreAuthorize("hasAnyRole('ROLE_CLIENTE', 'ROLE_ADMIN')")
    @GetMapping("/reserva/{propiedad_id}")
    public String mostrar_reserva_propiedad(@PathVariable String propiedad_id, ModelMap model) {
        Propiedad propiedad = propiedadServicio.getOne(propiedad_id);
        model.put("propiedad", propiedad);
        return "reserva.html";
    }
}
