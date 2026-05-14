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
        return detalleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DetalleResponse obtenerPorId(Long id) {
        return detalleRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Detalle de venta no encontrado con ID: " + id));
    }

    public List<DetalleResponse> obtenerPorVentaId(Long ventaId) {
        return detalleRepository.findByVentaId(ventaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DetalleResponse guardar(DetalleRequest request) {

        ventasClient.validarVenta(request.getVentaId());

        ProductoResponse producto = productoClient.obtenerProducto(request.getProductoId());

        BigDecimal subTotal = BigDecimal.valueOf(producto.getPrecio())
                .multiply(BigDecimal.valueOf(request.getCantidad()));

        DetalleModel detalle = DetalleModel.builder()
                .ventaId(request.getVentaId())
                .productoId(request.getProductoId())
                .cantidad(request.getCantidad())
                .precioUnitario(BigDecimal.valueOf(producto.getPrecio()))
                .subTotal(subTotal)
                .build();

        return toResponse(detalleRepository.save(detalle));
    }

    public DetalleResponse actualizar(Long id, DetalleRequest request) {
        DetalleModel detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Detalle de venta no encontrado con ID: " + id));

        ventasClient.validarVenta(request.getVentaId());

        ProductoResponse producto = productoClient.obtenerProducto(request.getProductoId());

        BigDecimal subTotal = BigDecimal.valueOf(producto.getPrecio())
                .multiply(BigDecimal.valueOf(request.getCantidad()));

        detalle.setVentaId(request.getVentaId());
        detalle.setProductoId(request.getProductoId());
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecio()));
        detalle.setSubTotal(subTotal);

        return toResponse(detalleRepository.save(detalle));
    }

    public void eliminar(Long id) {
        DetalleModel detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Detalle de venta no encontrado con ID: " + id));

        detalleRepository.delete(detalle);
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