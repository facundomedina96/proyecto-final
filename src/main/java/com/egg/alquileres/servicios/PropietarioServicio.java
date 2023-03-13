/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Propietario;
import com.egg.alquileres.entidades.Rol;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.PropietarioRepositorio;
import java.text.ParseException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hernan E Encizo
 */
@Service
public class PropietarioServicio {

    @Autowired
    private PropietarioRepositorio propietarioRepositorio;
    @Autowired
    private PropiedadServicio propiedadServicio;

    @Transactional
    public void registrarPropietario(String nombre, String apellido, String email, String password, String password2, String telefono) throws MiException {

        // cualquier usuario que se registre va a setearse por defecto como USER
        Propietario propietario = new Propietario();

        propietario.setNombreUsuario(nombre);
        propietario.setApellidoUsuario(apellido);
        propietario.setEmail(email);
        // cuando seteamos la clave la codificamos con el metodo configureGlobal de la clase SeguridadWeb
        propietario.setPassword(new BCryptPasswordEncoder().encode(password));
        propietario.setTelefono(telefono);
        propietario.setActivo(Boolean.TRUE);
        propietario.setRol(Rol.PROPIETARIO);

        // por ultimo persistimos el Usuario 
        propietarioRepositorio.save(propietario);
        // para poder autenticar al usuario hacemos que la clase UsuaruoServicio
        // implemente la interfaz UserDetailsService.
        // sobre escribimos el metodo loadUserByUsername el cual recibe el username de 
        // tipo String para que nosotros podamos autentificarlo.
    }
    
    public Propietario getOne(String id) {
        return propietarioRepositorio.getById(id);
    }
    
    public void modificar(String id, String nombre, String apellido, String email, String password, String password2, String telefono) throws MiException {
        // podria usar una validacion en la propia clase del clienteServicio pero como ya hay una en usuario
        UsuarioServicio usuarioServicio = new UsuarioServicio();
        
        usuarioServicio.validar(nombre, apellido, email, password, password2, telefono);
        
        Optional<Propietario> respuesta = propietarioRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            Propietario propietario = respuesta.get();
            propietario.setNombreUsuario(nombre);
            propietario.setApellidoUsuario(apellido);
            propietario.setEmail(email);
            propietario.setPassword(new BCryptPasswordEncoder().encode(password));
            propietario.setTelefono(telefono);
            
            propietarioRepositorio.save(propietario);
        } else {
            throw new MiException("No se encontro ningún usuario con ese ID");
        }
    }
    
    public void eliminar(String id) throws MiException {
       
        if (id == null || id.isEmpty()) {
            throw new MiException("El id no puede ser nulo o estar vacio");
        }
        
        Optional<Propietario> respuesta = propietarioRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            propietarioRepositorio.deleteById(id);
        } else {
            throw new MiException("No se encontro ningún usuario con ese ID");
        }
    }

    public void registrarPropiedad(String id, String nombre, String direccion, String ciudad, Double precio) throws MiException {
        try {
            Propietario propietario = propietarioRepositorio.findById(id).get();
            propiedadServicio.crearPropiedad(nombre, direccion, ciudad, precio, propietario);
        } catch (ParseException ex) {
            throw new MiException("No existe un Propietario con ese ID");
        }
    }

}
