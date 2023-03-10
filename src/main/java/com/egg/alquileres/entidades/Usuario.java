package com.egg.alquileres.entidades;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Luz
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")   
    protected String usuario_id;
    
    @Enumerated(EnumType.STRING)
    protected Rol rol;
    
    protected String nombreUsuario;
    protected String apellidoUsuario;
    protected String email;
    protected String password;
    protected String telefono;
    protected Boolean activo;

}
