/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.entidades;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class Propiedad {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String idPropiedad;
    private String nombre; 
    private String ubicacion;
    private Double precio_base;
    private Boolean estado;
    
    @OneToOne
    private Propietario propietario;
    
    @OneToMany
    private List<Prestacion> serviciosPrestados;
    
    @OneToMany
    private List<FechasDisponibles> fechasDisponibles;
    
}
