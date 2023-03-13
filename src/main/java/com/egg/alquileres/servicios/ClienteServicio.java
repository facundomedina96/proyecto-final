/*
    COPIA DEL PROYECTO EN MI COMPU ESTA NO TIENE NINGUNA CONEXION CON EL REPO
 */
package com.egg.alquileres.servicios;

import com.egg.alquileres.entidades.Cliente;
import com.egg.alquileres.entidades.Rol;
import com.egg.alquileres.excepciones.MiException;
import com.egg.alquileres.repositorios.ClienteRepositorio;
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
public class ClienteServicio {
    
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    
    @Transactional
    public void registrar(String nombre, String apellido, String email, String password, String password2, String telefono) throws MiException {

        // cualquier usuario que se registre va a setearse por defecto como USER
        Cliente cliente = new Cliente();
        
        cliente.setNombreUsuario(nombre);
        cliente.setApellidoUsuario(apellido);
        cliente.setEmail(email);
        // cuando seteamos la clave la codificamos con el metodo configureGlobal de la clase SeguridadWeb
        cliente.setPassword(new BCryptPasswordEncoder().encode(password));
        cliente.setTelefono(telefono);
        cliente.setActivo(Boolean.TRUE);
        cliente.setRol(Rol.CLIENTE);

        // por ultimo persistimos el Usuario 
        clienteRepositorio.save(cliente);
        
    }

    //devuelve un cliente podria usar solo este metodo
    public Cliente getOne(String id) {
        return clienteRepositorio.getById(id);
    }
    
    public void modificar(String id, String nombre, String apellido, String email, String password, String password2, String telefono) throws MiException {
        // podria usar una validacion en la propia clase del clienteServicio pero como ya hay una en usuario
        UsuarioServicio usuarioServicio = new UsuarioServicio();
        
        usuarioServicio.validar(nombre, apellido, email, password, password2, telefono);
        
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            Cliente cliente = respuesta.get();
            cliente.setNombreUsuario(nombre);
            cliente.setApellidoUsuario(apellido);
            cliente.setEmail(email);
            cliente.setPassword(new BCryptPasswordEncoder().encode(password));
            cliente.setTelefono(telefono);
            
            clienteRepositorio.save(cliente);
        } else {
            throw new MiException("No se encontro ningún usuario con ese ID");
        }
    }
    
    public void eliminar(String id) throws MiException {
       
        if (id == null || id.isEmpty()) {
            throw new MiException("El id no puede ser nulo o estar vacio");
        }
        
        Optional<Cliente> respuesta = clienteRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
            clienteRepositorio.deleteById(id);
        } else {
            throw new MiException("No se encontro ningún usuario con ese ID");
        }
    }
}
