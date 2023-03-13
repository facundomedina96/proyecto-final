package com.egg.alquileres.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Hernan E Encizo
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Propiedad implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String idPropiedad;
    @Column(nullable = false) // Agregar la anotación @Column(nullable = false)
    private String nombre;
    @Column(nullable = false) // Agregar la anotación @Column(nullable = false)
    private String direccion;
    @Column(nullable = false) // Agregar la anotación @Column(nullable = false)
    private String ciudad;
    @Column(nullable = false) // Agregar la anotación @Column(nullable = false)
    private Double precio_base;
    @Column(nullable = false) // Agregar la anotación @Column(nullable = false)
    private Boolean estado;

    @OneToOne
    private Propietario propietario;
    @ElementCollection
    @Temporal(TemporalType.DATE) // Agregar la anotación @Temporal(TemporalType.DATE)
    private Set<Date> fechasDisponibles; // Cambiar la lista a un conjunto Set

    @OneToMany
    private List<Reserva> reservasActivas;
}
