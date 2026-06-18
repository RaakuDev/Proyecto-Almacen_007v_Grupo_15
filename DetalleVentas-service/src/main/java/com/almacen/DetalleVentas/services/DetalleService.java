package com.almacen.DetalleVentas.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.almacen.DetalleVentas.dtos.request.DetalleRequest;
import com.almacen.DetalleVentas.dtos.response.DetalleResponse;
import com.almacen.DetalleVentas.dtos.response.ProductoResponse;
import com.almacen.DetalleVentas.exceptions.NotFoundException;
import com.almacen.DetalleVentas.models.DetalleModel;
import com.almacen.DetalleVentas.repositories.DetalleRepository;
import com.almacen.DetalleVentas.webclient.InventarioClient;
import com.almacen.DetalleVentas.webclient.ProductoClient;
import com.almacen.DetalleVentas.webclient.VentasClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DetalleService {

        private final DetalleRepository detalleRepository;
        private final ProductoClient productoClient;
        private final VentasClient ventasClient;
        private final InventarioClient inventarioClient;

        public DetalleService(
                        DetalleRepository detalleRepository,
                        ProductoClient productoClient,
                        VentasClient ventasClient,
                        InventarioClient inventarioClient) {
                this.detalleRepository = detalleRepository;
                this.productoClient = productoClient;
                this.ventasClient = ventasClient;
                this.inventarioClient = inventarioClient;
        }

        public List<DetalleResponse> obtenerTodos() {

                log.info("Obteniendo todos los detalles de venta");

                List<DetalleModel> detalles = detalleRepository.findAll();

                if (detalles.isEmpty()) {
                        log.error("No existen detalles de venta registrados");
                        throw new NotFoundException("No existen detalles de venta registrados");
                }

                return detalles.stream()
                                .map(this::toResponse)
                                .toList();
        }

        public DetalleResponse obtenerPorId(Long id) {

                if (id == null) {
                        log.error("El id del detalle de venta no puede ser nulo");
                        throw new NotFoundException("El id del detalle de venta no puede ser nulo");
                }

                if (id <= 0) {
                        log.error("El id del detalle de venta debe ser mayor a cero");
                        throw new NotFoundException("El id del detalle de venta debe ser mayor a cero");
                }

                log.info("Buscando detalle de venta con ID: {}", id);

                return detalleRepository.findById(id)
                                .map(this::toResponse)
                                .orElseThrow(() -> {
                                        log.error("Detalle de venta no encontrado con ID: {}", id);
                                        return new NotFoundException("Detalle de venta no encontrado con ID: " + id);
                                });
        }

        public List<DetalleResponse> obtenerPorVentaId(Long ventaId) {

                if (ventaId == null) {
                        log.error("El id de la venta no puede ser nulo");
                        throw new NotFoundException("El id de la venta no puede ser nulo");
                }

                if (ventaId <= 0) {
                        log.error("El id de la venta debe ser mayor a cero");
                        throw new NotFoundException("El id de la venta debe ser mayor a cero");
                }

                log.info("Obteniendo detalles asociados a la venta ID: {}", ventaId);

                ventasClient.validarVenta(ventaId);

                List<DetalleModel> detalles = detalleRepository.findByVentaId(ventaId);

                if (detalles.isEmpty()) {
                        log.error("No existen detalles asociados a la venta ID: {}", ventaId);
                        throw new NotFoundException("No existen detalles asociados a la venta ID: " + ventaId);
                }

                return detalles.stream()
                                .map(this::toResponse)
                                .toList();
        }

        public DetalleResponse guardar(DetalleRequest request) {

                if (request == null) {
                        log.error("Los datos del detalle de venta no pueden ser nulos");
                        throw new NotFoundException("Los datos del detalle de venta no pueden ser nulos");
                }

                if (request.getVentaId() == null || request.getVentaId() <= 0) {
                        log.error("El id de la venta es obligatorio y debe ser mayor a cero");
                        throw new NotFoundException("El id de la venta es obligatorio y debe ser mayor a cero");
                }

                if (request.getProductoId() == null || request.getProductoId() <= 0) {
                        log.error("El id del producto es obligatorio y debe ser mayor a cero");
                        throw new NotFoundException("El id del producto es obligatorio y debe ser mayor a cero");
                }

                if (request.getCantidad() == null || request.getCantidad() <= 0) {
                        log.error("La cantidad debe ser mayor a cero");
                        throw new NotFoundException("La cantidad debe ser mayor a cero");
                }

                log.info("Guardando detalle de venta para venta ID: {} y producto ID: {}",
                                request.getVentaId(),
                                request.getProductoId());

               // ventasClient.validarVenta(request.getVentaId());

                //log.info("Venta validada correctamente con ID: {}", request.getVentaId());

                ProductoResponse producto = productoClient.obtenerProducto(request.getProductoId());

                if (producto == null) {
                        log.error("No existe el producto con ID: {}", request.getProductoId());
                        throw new NotFoundException("No existe el producto con id: " + request.getProductoId());
                }

                if (producto.getPrecio() == null) {
                        log.error("El producto con ID {} no tiene precio definido", request.getProductoId());
                        throw new NotFoundException("El producto no tiene precio definido");
                }

                log.info("Producto obtenido correctamente con ID: {}", request.getProductoId());

                inventarioClient.descontarStock(
                                request.getProductoId(),
                                request.getCantidad());

                log.info("Stock descontado correctamente para producto ID: {}", request.getProductoId());

                BigDecimal subTotal = BigDecimal.valueOf(producto.getPrecio())
                                .multiply(BigDecimal.valueOf(request.getCantidad()));

                DetalleModel detalle = DetalleModel.builder()
                                .ventaId(request.getVentaId())
                                .productoId(request.getProductoId())
                                .cantidad(request.getCantidad())
                                .precioUnitario(BigDecimal.valueOf(producto.getPrecio()))
                                .subTotal(subTotal)
                                .build();

                DetalleModel guardado = detalleRepository.save(detalle);

                log.info("Detalle de venta guardado correctamente con ID: {}",
                                guardado.getIdDetalleVenta());

                return toResponse(guardado);
        }

        public DetalleResponse actualizar(Long id, DetalleRequest request) {

                if (id == null) {
                        log.error("El id del detalle de venta no puede ser nulo");
                        throw new NotFoundException("El id del detalle de venta no puede ser nulo");
                }

                if (id <= 0) {
                        log.error("El id del detalle de venta debe ser mayor a cero");
                        throw new NotFoundException("El id del detalle de venta debe ser mayor a cero");
                }

                if (request == null) {
                        log.error("Los datos del detalle de venta no pueden ser nulos");
                        throw new NotFoundException("Los datos del detalle de venta no pueden ser nulos");
                }

                if (request.getVentaId() == null || request.getVentaId() <= 0) {
                        log.error("El id de la venta es obligatorio y debe ser mayor a cero");
                        throw new NotFoundException("El id de la venta es obligatorio y debe ser mayor a cero");
                }

                if (request.getProductoId() == null || request.getProductoId() <= 0) {
                        log.error("El id del producto es obligatorio y debe ser mayor a cero");
                        throw new NotFoundException("El id del producto es obligatorio y debe ser mayor a cero");
                }

                if (request.getCantidad() == null || request.getCantidad() <= 0) {
                        log.error("La cantidad debe ser mayor a cero");
                        throw new NotFoundException("La cantidad debe ser mayor a cero");
                }

                log.info("Actualizando detalle de venta con ID: {}", id);

                DetalleModel detalle = detalleRepository.findById(id)
                                .orElseThrow(() -> {
                                        log.error("Detalle de venta no encontrado con ID: {}", id);
                                        return new NotFoundException("Detalle de venta no encontrado con ID: " + id);
                                });

                ventasClient.validarVenta(request.getVentaId());

                log.info("Venta validada correctamente con ID: {}", request.getVentaId());

                ProductoResponse producto = productoClient.obtenerProducto(request.getProductoId());

                if (producto == null) {
                        log.error("No existe el producto con ID: {}", request.getProductoId());
                        throw new NotFoundException("No existe el producto con id: " + request.getProductoId());
                }

                if (producto.getPrecio() == null) {
                        log.error("El producto con ID {} no tiene precio definido", request.getProductoId());
                        throw new NotFoundException("El producto no tiene precio definido");
                }

                log.info("Producto obtenido correctamente con ID: {}", request.getProductoId());

                BigDecimal subTotal = BigDecimal.valueOf(producto.getPrecio())
                                .multiply(BigDecimal.valueOf(request.getCantidad()));

                detalle.setVentaId(request.getVentaId());
                detalle.setProductoId(request.getProductoId());
                detalle.setCantidad(request.getCantidad());
                detalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecio()));
                detalle.setSubTotal(subTotal);

                DetalleModel actualizado = detalleRepository.save(detalle);

                log.info("Detalle de venta actualizado correctamente con ID: {}",
                                actualizado.getIdDetalleVenta());

                return toResponse(actualizado);
        }

        public void eliminar(Long id) {

                if (id == null) {
                        log.error("El id del detalle de venta no puede ser nulo");
                        throw new NotFoundException("El id del detalle de venta no puede ser nulo");
                }

                if (id <= 0) {
                        log.error("El id del detalle de venta debe ser mayor a cero");
                        throw new NotFoundException("El id del detalle de venta debe ser mayor a cero");
                }

                log.info("Eliminando detalle de venta con ID: {}", id);

                DetalleModel detalle = detalleRepository.findById(id)
                                .orElseThrow(() -> {
                                        log.error("Detalle de venta no encontrado con ID: {}", id);
                                        return new NotFoundException("Detalle de venta no encontrado con ID: " + id);
                                });

                detalleRepository.delete(detalle);

                log.info("Detalle de venta eliminado correctamente con ID: {}", id);
        }

        private DetalleResponse toResponse(DetalleModel detalle) {

                if (detalle == null) {
                        log.error("El detalle de venta no puede ser nulo");
                        throw new NotFoundException("El detalle de venta no puede ser nulo");
                }

                ProductoResponse producto = productoClient.obtenerProducto(detalle.getProductoId());

                if (producto == null) {
                        log.error("No existe el producto con ID: {}", detalle.getProductoId());
                        throw new NotFoundException("No existe el producto con id: " + detalle.getProductoId());
                }

                return DetalleResponse.builder()
                                .idDetalle(detalle.getIdDetalleVenta())
                                .ventaId(detalle.getVentaId())
                                .productoId(detalle.getProductoId())
                                .nombreProducto(producto.getNombre())
                                .cantidad(detalle.getCantidad())
                                .precioUnitario(detalle.getPrecioUnitario())
                                .subTotal(detalle.getSubTotal())
                                .build();
        }
}