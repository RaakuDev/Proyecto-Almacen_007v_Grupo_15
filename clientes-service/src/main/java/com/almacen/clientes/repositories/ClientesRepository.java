package com.almacen.clientes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.almacen.clientes.models.ClientesModel;

@Repository
public interface ClientesRepository extends JpaRepository<ClientesModel, Long> {

}