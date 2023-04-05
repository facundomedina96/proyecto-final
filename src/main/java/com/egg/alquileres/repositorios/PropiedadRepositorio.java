package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Propiedad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropiedadRepositorio extends JpaRepository<Propiedad, String> {

    @Query("SELECT p FROM Propiedad p WHERE p.propietario.id = :id")
    public List<Propiedad> buscarPorPropietario(@Param("id") String id);

    @Query("SELECT p FROM Propiedad p WHERE p.propietario.activo = TRUE")
    public List<Propiedad> buscarPorPropietariosActivos();

    @Query("SELECT p FROM Propiedad p WHERE p.ciudad = :ciudad")
    public List<Propiedad> buscarPorCiudad(@Param("ciudad") String ciudad);

    @Query("SELECT p FROM Propiedad p WHERE p.ciudad = :ciudad AND p.propietario.id= :id")
    public List<Propiedad> buscarPorPropietarioYCiudad(@Param("id") String id, @Param("ciudad") String ciudad);

    @Query("SELECT p FROM Propiedad p WHERE p.precio_base <= :precioMax")
    public List<Propiedad> buscarPorPrecioMax(@Param("precioMax") Double precioMax);

    @Query("SELECT p FROM Propiedad p WHERE p.ciudad = :ciudad AND p.propietario.id= :id AND p.precio_base<= :precio")
    public List<Propiedad> buscarPorPropietarioCiudadPrecio(@Param("id") String id, @Param("ciudad") String ciudad, @Param("precio") Double precio);

    @Query("SELECT p FROM Propiedad p WHERE p.propietario.id= :id AND p.precio_base<= :precio")
    public List<Propiedad> buscarPorPropietarioYPrecio(@Param("id") String id, @Param("precio") Double precio);

    @Query("SELECT p FROM Propiedad p WHERE p.ciudad = :ciudad AND p.precio_base<= :precio")
    public List<Propiedad> buscarPorCiudadYPrecio(@Param("ciudad") String ciudad, @Param("precio") Double precio);
}
