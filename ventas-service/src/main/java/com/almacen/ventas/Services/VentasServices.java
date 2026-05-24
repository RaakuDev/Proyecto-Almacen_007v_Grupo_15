package com.almacen.ventas.Services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.almacen.ventas.dtos.response.ClienteResponse;
import com.almacen.ventas.dtos.response.EmpleadoResponse;
import com.almacen.ventas.webclient.EmpleadoClient;
import org.springframework.stereotype.Service;

import com.almacen.ventas.dtos.request.VentasRequest;
import com.almacen.ventas.dtos.response.DetalleVentaResponse;
import com.almacen.ventas.dtos.response.VentasResponse;
import com.almacen.ventas.enums.EstadoVentas;
import com.almacen.ventas.exceptions.NotFoundException;
import com.almacen.ventas.models.VentasModel;
import com.almacen.ventas.repositories.VentasRepository;
import com.almacen.ventas.webclient.ClienteClient;
import com.almacen.ventas.webclient.DetalleVentaClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VentasServices {

    private final VentasRepository ventasRepository;
    private final ClienteClient clienteClient;
    private final DetalleVentaClient detalleVentaClient;
    private final EmpleadoClient empleadoClient;

    public VentasServices(
            VentasRepository ventasRepository,
            ClienteClient clienteClient,
            DetalleVentaClient detalleVentaClient, EmpleadoClient empleadoClient) {
        this.ventasRepository = ventasRepository;
        this.clienteClient = clienteClient;
        this.detalleVentaClient = detalleVentaClient;
        this.empleadoClient = empleadoClient;
    }

    public List<VentasResponse> obtenerTodas() {
        log.info("Obteniendo todas las ventas");

        return ventasRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VentasResponse obtenerPorId(Long id) {
        log.info("Buscando venta con ID: {}", id);

        return ventasRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.error("No existe la venta con ID: {}", id);
                    return new NotFoundException("No existe la venta con ID: " + id);
                });
    }

    public VentasResponse guardar(VentasRequest request) {

        log.info("Guardando nueva venta");

        if (request.getClienteId() != null) {

            log.info("Cliente validado correctamente con ID: {}",
                    request.getClienteId());

        } else {

            log.info("Venta sin cliente asociado");
        }

        VentasModel venta = VentasModel.builder()
                .fechaVenta(LocalDateTime.now())
                .subTotal(BigDecimal.ZERO)
                .descuentoTotal(BigDecimal.ZERO)
                .impuestoTotal(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .metodoPago(request.getMetodoPago())
                .tipoComprobante(request.getTipoComprobante())
                .montoPagado(request.getMontoPagado())
                .vuelto(BigDecimal.ZERO)
                .estadoVenta(EstadoVentas.COMPLETADA)
                .clienteId(request.getClienteId())
                .empleadoId(request.getEmpleadoId())
                .numeroComprobante(request.getNumeroComprobante())
                .observaciones(request.getObservaciones())
                .build();

        VentasModel guardada = ventasRepository.save(venta);

        log.info("Venta creada correctamente con ID: {}",
                guardada.getIdVenta());

        request.getItems().forEach(item -> {

            detalleVentaClient.crearDetalle(

                    com.almacen.ventas.dtos.request.DetalleVentaRequest.builder()
                            .ventaId(guardada.getIdVenta())
                            .productoId(item.getProductoId())
                            .cantidad(item.getCantidad())
                            .build());

            log.info("Detalle creado automáticamente para producto ID: {}",
                    item.getProductoId());
        });

        return recalcularTotal(guardada.getIdVenta());
    }

    public VentasResponse actualizar(Long id, VentasRequest request) {
        log.info("Actualizando venta con ID: {}", id);

        VentasModel venta = ventasRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe la venta con ID: {}", id);
                    return new NotFoundException("No existe la venta con ID: " + id);
                });

        if (request.getClienteId() != null) {
            log.info("Cliente validado correctamente con ID: {}", request.getClienteId());
        } else {
            log.info("Venta actualizada sin cliente asociado");
        }

        venta.setSubTotal(request.getSubTotal());
        venta.setDescuentoTotal(request.getDescuentoTotal());
        venta.setImpuestoTotal(request.getImpuestoTotal());
        venta.setTotal(request.getTotal());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setTipoComprobante(request.getTipoComprobante());
        venta.setMontoPagado(request.getMontoPagado());
        venta.setVuelto(request.getVuelto());
        venta.setClienteId(request.getClienteId());
        venta.setEmpleadoId(request.getEmpleadoId());
        venta.setNumeroComprobante(request.getNumeroComprobante());
        venta.setObservaciones(request.getObservaciones());

        VentasModel actualizada = ventasRepository.save(venta);

        log.info("Venta actualizada correctamente con ID: {}", actualizada.getIdVenta());

        return toResponse(actualizada);
    }

    public VentasResponse obtenerPorNumeroComprobante(String numeroComprobante) {
        return ventasRepository.findByNumeroComprobante(numeroComprobante)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException(
                        "No existe una venta con comprobante: " + numeroComprobante));
    }

    public VentasResponse recalcularTotal(Long idVenta) {
        log.info("Recalculando total de venta con ID: {}", idVenta);

        VentasModel venta = ventasRepository.findById(idVenta)
                .orElseThrow(() -> {
                    log.error("No existe la venta con ID: {}", idVenta);
                    return new NotFoundException("No existe la venta con ID: " + idVenta);
                });

        List<DetalleVentaResponse> detalles = detalleVentaClient.obtenerDetallesPorVenta(idVenta);

        log.info("Detalles obtenidos correctamente para venta ID: {}", idVenta);

        BigDecimal subTotal = detalles.stream()
                .map(DetalleVentaResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = venta.getDescuentoTotal() != null
                ? venta.getDescuentoTotal()
                : BigDecimal.ZERO;

        BigDecimal impuesto = subTotal.multiply(BigDecimal.valueOf(0.19));

        BigDecimal total = subTotal.subtract(descuento).add(impuesto);

        total = BigDecimal.valueOf(
                Math.round(total.doubleValue() / 10.0) * 10);

        BigDecimal montoPagado = venta.getMontoPagado() != null
                ? venta.getMontoPagado()
                : BigDecimal.ZERO;

        BigDecimal vuelto = montoPagado.subtract(total);

        venta.setSubTotal(subTotal);
        venta.setImpuestoTotal(impuesto);
        venta.setTotal(total);
        venta.setVuelto(vuelto);

        VentasModel recalculada = ventasRepository.save(venta);

        log.info("Venta recalculada correctamente con ID: {}", recalculada.getIdVenta());

        return toResponse(recalculada);
    }

    public void eliminar(Long id) {
        log.info("Eliminando venta con ID: {}", id);

        VentasModel venta = ventasRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe la venta con ID: {}", id);
                    return new NotFoundException("No existe la venta con ID: " + id);
                });

        ventasRepository.delete(venta);

        log.info("Venta eliminada correctamente con ID: {}", id);
    }

    private VentasResponse toResponse(VentasModel venta) {
        List<DetalleVentaResponse> detalles =
                detalleVentaClient.obtenerDetallesPorVenta(venta.getIdVenta());

        // Obtener cliente si existe
        ClienteResponse cliente = null;
        if (venta.getClienteId() != null) {
            try {
                cliente = clienteClient.obtenerClientePorId(venta.getClienteId());
            } catch (Exception e) {
                log.error("Error al obtener cliente con id: {}", venta.getClienteId());
            }
        }

        // Obtener empleado si existe
        EmpleadoResponse empleado = null;
        if (venta.getEmpleadoId() != null) {
            try {
                empleado = empleadoClient.obtenerEmpleadoPorId(venta.getEmpleadoId());
            } catch (Exception e) {
                log.error("Error al obtener empleado con id: {}", venta.getEmpleadoId());
            }
        }

        return VentasResponse.builder()
                .idVenta(venta.getIdVenta())
                .fechaVenta(venta.getFechaVenta())
                .subTotal(venta.getSubTotal())
                .descuentoTotal(venta.getDescuentoTotal())
                .impuestoTotal(venta.getImpuestoTotal())
                .total(venta.getTotal())
                .metodoPago(venta.getMetodoPago())
                .tipoComprobante(venta.getTipoComprobante())
                .montoPagado(venta.getMontoPagado())
                .vuelto(venta.getVuelto())
                .estadoVenta(venta.getEstadoVenta())
                .clienteId(venta.getClienteId())
                .empleadoId(venta.getEmpleadoId())
                .numeroComprobante(venta.getNumeroComprobante())
                .observaciones(venta.getObservaciones())
                .detalles(detalles)
                .cliente(cliente)
                .empleado(empleado)
                .build();
    }

}