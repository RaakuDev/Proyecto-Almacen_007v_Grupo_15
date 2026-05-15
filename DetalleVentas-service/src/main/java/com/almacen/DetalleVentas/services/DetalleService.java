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
import com.almacen.DetalleVentas.webclient.ProductoClient;
import com.almacen.DetalleVentas.webclient.VentasClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DetalleService {

    private final DetalleRepository detalleRepository;
    private final ProductoClient productoClient;
    private final VentasClient ventasClient;

    public DetalleService(DetalleRepository detalleRepository,
                          ProductoClient productoClient,
                          VentasClient ventasClient) {
        this.detalleRepository = detalleRepository;
        this.productoClient = productoClient;
        this.ventasClient = ventasClient;
    }

    public List<DetalleResponse> obtenerTodos() {

        log.info("Obteniendo todos los detalles de venta");

        return detalleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DetalleResponse obtenerPorId(Long id) {

        log.info("Buscando detalle de venta con ID: {}", id);

        return detalleRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {

                    log.error("Detalle de venta no encontrado con ID: {}", id);

                    return new NotFoundException(
                            "Detalle de venta no encontrado con ID: " + id
                    );
                });
    }

    public List<DetalleResponse> obtenerPorVentaId(Long ventaId) {

        log.info("Obteniendo detalles asociados a la venta ID: {}", ventaId);

        return detalleRepository.findByVentaId(ventaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DetalleResponse guardar(DetalleRequest request) {

        log.info(
                "Guardando detalle de venta para venta ID: {} y producto ID: {}",
                request.getVentaId(),
                request.getProductoId()
        );

        ventasClient.validarVenta(request.getVentaId());

        log.info("Venta validada correctamente con ID: {}", request.getVentaId());

        ProductoResponse producto =
                productoClient.obtenerProducto(request.getProductoId());

        log.info("Producto obtenido correctamente con ID: {}", request.getProductoId());

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

        log.info(
                "Detalle de venta guardado correctamente con ID: {}",
                guardado.getIdDetalleVenta()
        );

        return toResponse(guardado);
    }

    public DetalleResponse actualizar(Long id, DetalleRequest request) {

        log.info("Actualizando detalle de venta con ID: {}", id);

        DetalleModel detalle = detalleRepository.findById(id)
                .orElseThrow(() -> {

                    log.error("Detalle de venta no encontrado con ID: {}", id);

                    return new NotFoundException(
                            "Detalle de venta no encontrado con ID: " + id
                    );
                });

        ventasClient.validarVenta(request.getVentaId());

        log.info("Venta validada correctamente con ID: {}", request.getVentaId());

        ProductoResponse producto =
                productoClient.obtenerProducto(request.getProductoId());

        log.info("Producto obtenido correctamente con ID: {}", request.getProductoId());

        BigDecimal subTotal = BigDecimal.valueOf(producto.getPrecio())
                .multiply(BigDecimal.valueOf(request.getCantidad()));

        detalle.setVentaId(request.getVentaId());
        detalle.setProductoId(request.getProductoId());
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecio()));
        detalle.setSubTotal(subTotal);

        DetalleModel actualizado = detalleRepository.save(detalle);

        log.info(
                "Detalle de venta actualizado correctamente con ID: {}",
                actualizado.getIdDetalleVenta()
        );

        return toResponse(actualizado);
    }

    public void eliminar(Long id) {

        log.info("Eliminando detalle de venta con ID: {}", id);

        DetalleModel detalle = detalleRepository.findById(id)
                .orElseThrow(() -> {

                    log.error("Detalle de venta no encontrado con ID: {}", id);

                    return new NotFoundException(
                            "Detalle de venta no encontrado con ID: " + id
                    );
                });

        detalleRepository.delete(detalle);

        log.info("Detalle de venta eliminado correctamente con ID: {}", id);
    }

    private DetalleResponse toResponse(DetalleModel detalle) {

        return DetalleResponse.builder()
                .idDetalle(detalle.getIdDetalleVenta())
                .ventaId(detalle.getVentaId())
                .productoId(detalle.getProductoId())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subTotal(detalle.getSubTotal())
                .build();
    }
}