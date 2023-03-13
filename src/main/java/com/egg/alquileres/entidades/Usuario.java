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
    // NOTA: cambiar el usuario_id solo por id, el nombreUsuario por nombre etc, utilizar convencion JPA  
    // ademas trae inconvenientes en la comprension, por ej (Thymelife cliente.usuario_id) seria mas facil
    // comprender cliente.id, etc...
    protected String nombreUsuario;
    protected String apellidoUsuario;
    protected String email;
    protected String password;
    protected String telefono;
    protected Boolean activo;
}
