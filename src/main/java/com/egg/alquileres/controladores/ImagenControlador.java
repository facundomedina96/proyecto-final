package com.egg.alquileres.controladores;

import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.servicios.ImagenServicio;
import com.egg.alquileres.servicios.PropiedadServicio;
import com.egg.alquileres.servicios.UsuarioServicio;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/imagen")
public class ImagenControlador {

    private final PropiedadServicio propiedadServicio;
    private final UsuarioServicio usuarioServicio;
    private final ImagenServicio imagenServicio;

    public ImagenControlador(PropiedadServicio propiedadServicio, UsuarioServicio usuarioServicio, ImagenServicio imagenServicio) {
        this.propiedadServicio = propiedadServicio;
        this.usuarioServicio = usuarioServicio;
        this.imagenServicio = imagenServicio;
    }
    
    /**
     * Función general para presentar una imagen.
     * @param id id de la imagen.
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> imagenMostrar(@PathVariable String id) {

        byte[] imagen = imagenServicio.buscarImagen(id).getContenido();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
    }

    @GetMapping("/perfil/{id}")
    public ResponseEntity<byte[]> imagenPerfil(@PathVariable String id) {
        Usuario usuario = usuarioServicio.getOne(id);

        byte[] imagen = usuario.getFoto_perfil().getContenido();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
    }
}
