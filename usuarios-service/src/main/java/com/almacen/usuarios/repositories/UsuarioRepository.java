package com.almacen.usuarios.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.almacen.usuarios.models.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long>{
    
    Optional<UsuarioModel> findByUsername(String username);

    Optional<UsuarioModel> findByRut(String rut);

    Optional<UsuarioModel> findByEmail(String email);

}



// Optional permite manejar búsquedas seguras evitando errores null cuando no se encuentra información.
// En pocas palabras,  si existe lo devuelvo, si no, lo devuelvo vacio... pero evita el error nullPointerException