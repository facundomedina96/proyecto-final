package com.egg.alquileres.repositorios;

import com.egg.alquileres.entidades.Usuario;
import com.egg.alquileres.enumeraciones.Rol;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario buscarPorEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u")
    public List<Usuario> buscarUsuarios();

    @Query("SELECT u FROM Usuario u WHERE u.rol = CLIENTE")
    public List<Usuario> buscarClientes();

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol")
    public List<Usuario> buscarPropietarios(@Param("rol") Rol rol);
    
    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.activo = TRUE")
    public List<Usuario> buscarPropietariosActivos(@Param("rol") Rol rol);
}
