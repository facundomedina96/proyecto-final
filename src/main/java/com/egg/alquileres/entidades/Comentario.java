
package com.egg.alquileres.entidades;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author agofa
 */
@Entity
@NoArgsConstructor //genera un constructor sin parametros
@AllArgsConstructor //con parametros
@Getter
@Setter
public class Comentario implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid") 
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
   
    private String opinion;
    private int calificacion;
    
    @OneToOne
    private Imagen imagen;
}