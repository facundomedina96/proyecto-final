package com.egg.alquileres.entidades;

import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Luz
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Propietario extends Usuario {

    @OneToMany
    private Set<Propiedad> propiedades;

    @OneToMany
    private List<Reserva> reservas;
}
