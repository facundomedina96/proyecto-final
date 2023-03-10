package com.egg.alquileres.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.ElementCollection;
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
    private String nombre; 
    private String direccion;
    private String ciudad;
    private Double precio_base;
    private Boolean estado;
    
    @OneToOne
    private Propietario propietario;
    @ElementCollection
    private List<Date> fechasDisponibles;
    
    @OneToMany
    private List<Reserva> reservasActivas;
}
