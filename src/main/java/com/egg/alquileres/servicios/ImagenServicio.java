package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Imagen;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.ImagenRepositorio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagenServicio {

    private final ImagenRepositorio imagenRepositorio;

    public ImagenServicio(ImagenRepositorio imagenRepositorio) throws MiException {
        this.imagenRepositorio = imagenRepositorio;
    }

    @Transactional
    public Imagen crearImagen(MultipartFile archivo) {
        if (archivo != null) {
            try {
                Imagen imagen = new Imagen();
                imagen.setContenido(archivo.getBytes());
                imagen.setFormato(archivo.getContentType());
                imagen.setNombre(archivo.getName());

                imagenRepositorio.save(imagen);
                return imagen;

            } catch (IOException e) {
                System.err.println(e.getMessage()); //err es para la salida de errores
            }
        }
        return null;
    }

    public List<Imagen> listarImagenes() {
        List<Imagen> imagenes = new ArrayList();
        imagenes = imagenRepositorio.findAll();
        return imagenes;
    }

    public Imagen buscarImagen(String idImagen) {
        return imagenRepositorio.getOne(idImagen);
    }

    @Transactional
    public Imagen actualizarImagen(MultipartFile archivo, String idImagen) throws MiException {
        if (archivo != null) {
            try {
                Imagen imagen = new Imagen();
                if (idImagen != null) {
                    Optional<Imagen> respuesta = imagenRepositorio.findById(idImagen);
                    if (respuesta.isPresent()) {
                        imagen = respuesta.get();
                    }
                }
                imagen.setFormato(archivo.getContentType());

                imagen.setNombre(archivo.getName());

                imagen.setContenido(archivo.getBytes());

                return imagenRepositorio.save(imagen);

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    @Transactional
    public void eliminarImagen(String idImagen) {
        imagenRepositorio.deleteById(idImagen);
    }
}
