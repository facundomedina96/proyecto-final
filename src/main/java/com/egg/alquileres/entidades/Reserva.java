/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.alquileres.entidades;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
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
public class Reserva {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String idReserva;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaDesde;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaHasta;
    @OneToMany
    private List<Prestacion> prestacionesContratadas;

    @OneToOne
    private Propietario propietario;
    @OneToOne
    private Cliente cliente;
    private String comentario;

}
