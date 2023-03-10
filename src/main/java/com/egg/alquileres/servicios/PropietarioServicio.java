/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Cliente;
import com.egg.alquileres.entidades.Propietario;
import com.egg.alquileres.entidades.Rol;
import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.PropietarioRepositorio;
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
}
