package com.almacen.pedidos_service.repository;

import com.almacen.pedidos_service.model.PedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {

    List<PedidoModel> findByClienteId(Long clienteId);

    List<PedidoModel> findByEstado(String estado);
}