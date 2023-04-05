package com.egg.alquileres.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Propiedad implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
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
    private Usuario propietario;

    // si solo vamos a guardar dos dias parece inecesario que sea un set de fechas;
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
    @Temporal(TemporalType.DATE)
    private Date fechaFinAnio;

    @OneToMany
    private List<Reserva> reservasActivas;

    @OneToMany
    private Set<Imagen> fotos;

    @OneToMany
    private List<Prestacion> prestaciones;

    @OneToMany
    private List<Comentario> opiniones;

    public Propiedad() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Double getPrecio_base() {
        return precio_base;
    }

    public void setPrecio_base(Double precio_base) {
        this.precio_base = precio_base;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaFinAnio() {
        return fechaFinAnio;
    }

    public void setFechaFinAnio(Date fechaFinAnio) {
        this.fechaFinAnio = fechaFinAnio;
    }

    public List<Reserva> getReservasActivas() {
        return reservasActivas;
    }

    public void setReservasActivas(List<Reserva> reservasActivas) {
        this.reservasActivas = reservasActivas;
    }

    public Set<Imagen> getFotos() {
        return fotos;
    }

    public void setFotos(Set<Imagen> fotos) {
        this.fotos = fotos;
    }

    public List<Prestacion> getPrestaciones() {
        return prestaciones;
    }

    public void setPrestaciones(List<Prestacion> prestaciones) {
        this.prestaciones = prestaciones;
    }

    public List<Comentario> getOpiniones() {
        return opiniones;
    }

    public void setOpiniones(List<Comentario> opiniones) {
        this.opiniones = opiniones;
    }
}
