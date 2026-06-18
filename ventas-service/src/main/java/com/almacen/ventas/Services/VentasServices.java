package com.almacen.ventas.Services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.almacen.ventas.dtos.request.DetalleVentaRequest;
import com.almacen.ventas.dtos.request.ItemVentaRequest;
import com.almacen.ventas.dtos.request.VentasRequest;
import com.almacen.ventas.dtos.response.ClienteResponse;
import com.almacen.ventas.dtos.response.DetalleVentaResponse;
import com.almacen.ventas.dtos.response.EmpleadoResponse;
import com.almacen.ventas.dtos.response.VentasResponse;
import com.almacen.ventas.enums.EstadoVentas;
import com.almacen.ventas.exceptions.NotFoundException;
import com.almacen.ventas.models.VentasModel;
import com.almacen.ventas.repositories.VentasRepository;
import com.almacen.ventas.webclient.ClienteClient;
import com.almacen.ventas.webclient.DetalleVentaClient;
import com.almacen.ventas.webclient.EmpleadoClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            DetalleVentaClient detalleVentaClient,
            EmpleadoClient empleadoClient) {
        this.ventasRepository = ventasRepository;
        this.clienteClient = clienteClient;
        this.detalleVentaClient = detalleVentaClient;
        this.empleadoClient = empleadoClient;
    }

    public List<VentasResponse> obtenerTodas() {
        log.info("Obteniendo todas las ventas");

        List<VentasModel> ventas = ventasRepository.findAll();

        if (ventas.isEmpty()) {
            log.error("No existen ventas registradas");
            throw new NotFoundException("No existen ventas registradas");
        }

        return ventas.stream()
                .map(this::toResponse)
                .toList();
    }

    public VentasResponse obtenerPorId(Long id) {

        if (id == null) {
            log.error("El id de la venta no puede ser nulo");
            throw new NotFoundException("El id de la venta no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id de la venta debe ser mayor a cero");
            throw new NotFoundException("El id de la venta debe ser mayor a cero");
        }

        log.info("Buscando venta con ID: {}", id);

        return ventasRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.error("No existe la venta con ID: {}", id);
                    return new NotFoundException("No existe la venta con ID: " + id);
                });
    }

    public VentasResponse guardar(VentasRequest request) {

        if (request == null) {
            log.error("Los datos de la venta no pueden ser nulos");
            throw new NotFoundException("Los datos de la venta no pueden ser nulos");
        }

        if (request.getClienteId() != null && request.getClienteId() <= 0) {
            log.error("El id del cliente debe ser mayor a cero");
            throw new NotFoundException("El id del cliente debe ser mayor a cero");
        }

        if (request.getEmpleadoId() == null || request.getEmpleadoId() <= 0) {
            log.error("El id del empleado es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id del empleado es obligatorio y debe ser mayor a cero");
        }

        if (request.getMetodoPago() == null) {
            log.error("El método de pago es obligatorio");
            throw new NotFoundException("El método de pago es obligatorio");
        }

        if (request.getTipoComprobante() == null) {
            log.error("El tipo de comprobante es obligatorio");
            throw new NotFoundException("El tipo de comprobante es obligatorio");
        }

        if (request.getMontoPagado() == null) {
            log.error("El monto pagado es obligatorio");
            throw new NotFoundException("El monto pagado es obligatorio");
        }

        if (request.getMontoPagado().compareTo(BigDecimal.ZERO) < 0) {
            log.error("El monto pagado no puede ser negativo");
            throw new NotFoundException("El monto pagado no puede ser negativo");
        }

        if (request.getNumeroComprobante() == null || request.getNumeroComprobante().trim().isEmpty()) {
            log.error("El número de comprobante es obligatorio");
            throw new NotFoundException("El número de comprobante es obligatorio");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.error("La venta debe tener al menos un item");
            throw new NotFoundException("La venta debe tener al menos un item");
        }

        for (ItemVentaRequest item : request.getItems()) {

            if (item == null) {
                log.error("El item de la venta no puede ser nulo");
                throw new NotFoundException("El item de la venta no puede ser nulo");
            }

            if (item.getProductoId() == null || item.getProductoId() <= 0) {
                log.error("El id del producto es obligatorio y debe ser mayor a cero");
                throw new NotFoundException("El id del producto es obligatorio y debe ser mayor a cero");
            }

            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                log.error("La cantidad del producto debe ser mayor a cero");
                throw new NotFoundException("La cantidad del producto debe ser mayor a cero");
            }
        }

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

        log.info("Venta creada correctamente con ID: {}", guardada.getIdVenta());

        request.getItems().forEach(item -> {

            detalleVentaClient.crearDetalle(
                    DetalleVentaRequest.builder()
                            .ventaId(guardada.getIdVenta())
                            .productoId(item.getProductoId())
                            .cantidad(item.getCantidad())
                            .build());

            log.info("Detalle creado automáticamente para producto ID: {}", item.getProductoId());
        });

        return recalcularTotal(guardada.getIdVenta());
    }

    public VentasResponse actualizar(Long id, VentasRequest request) {

        if (id == null) {
            log.error("El id de la venta no puede ser nulo");
            throw new NotFoundException("El id de la venta no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id de la venta debe ser mayor a cero");
            throw new NotFoundException("El id de la venta debe ser mayor a cero");
        }

        if (request == null) {
            log.error("Los datos de la venta no pueden ser nulos");
            throw new NotFoundException("Los datos de la venta no pueden ser nulos");
        }

        if (request.getClienteId() != null && request.getClienteId() <= 0) {
            log.error("El id del cliente debe ser mayor a cero");
            throw new NotFoundException("El id del cliente debe ser mayor a cero");
        }

        if (request.getEmpleadoId() == null || request.getEmpleadoId() <= 0) {
            log.error("El id del empleado es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id del empleado es obligatorio y debe ser mayor a cero");
        }

        if (request.getMetodoPago() == null) {
            log.error("El método de pago es obligatorio");
            throw new NotFoundException("El método de pago es obligatorio");
        }

        if (request.getTipoComprobante() == null) {
            log.error("El tipo de comprobante es obligatorio");
            throw new NotFoundException("El tipo de comprobante es obligatorio");
        }

        if (request.getMontoPagado() == null) {
            log.error("El monto pagado es obligatorio");
            throw new NotFoundException("El monto pagado es obligatorio");
        }

        if (request.getMontoPagado().compareTo(BigDecimal.ZERO) < 0) {
            log.error("El monto pagado no puede ser negativo");
            throw new NotFoundException("El monto pagado no puede ser negativo");
        }

        if (request.getNumeroComprobante() == null || request.getNumeroComprobante().trim().isEmpty()) {
            log.error("El número de comprobante es obligatorio");
            throw new NotFoundException("El número de comprobante es obligatorio");
        }

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

<<<<<<< Updated upstream
        venta.setSubTotal(request.getSubTotal());
        venta.setDescuentoTotal(request.getDescuentoTotal());
        venta.setImpuestoTotal(request.getImpuestoTotal());
        venta.setTotal(request.getTotal());
=======
        empleadoClient.obtenerEmpleadoPorId(request.getEmpleadoId());
        log.info("Empleado validado correctamente con ID: {}", request.getEmpleadoId());

>>>>>>> Stashed changes
        venta.setMetodoPago(request.getMetodoPago());
        venta.setTipoComprobante(request.getTipoComprobante());
        venta.setMontoPagado(request.getMontoPagado());
        venta.setClienteId(request.getClienteId());
        venta.setEmpleadoId(request.getEmpleadoId());
        venta.setNumeroComprobante(request.getNumeroComprobante());
        venta.setObservaciones(request.getObservaciones());

        VentasModel actualizada = ventasRepository.save(venta);

        log.info("Venta actualizada correctamente con ID: {}", actualizada.getIdVenta());

        return recalcularTotal(actualizada.getIdVenta());
    }

    public VentasResponse obtenerPorNumeroComprobante(String numeroComprobante) {

        if (numeroComprobante == null || numeroComprobante.trim().isEmpty()) {
            log.error("El número de comprobante no puede ser nulo o vacío");
            throw new NotFoundException("El número de comprobante no puede ser nulo o vacío");
        }

        return ventasRepository.findByNumeroComprobante(numeroComprobante)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException(
                        "No existe una venta con comprobante: " + numeroComprobante));
    }

    public VentasResponse recalcularTotal(Long idVenta) {

        if (idVenta == null) {
            log.error("El id de la venta no puede ser nulo");
            throw new NotFoundException("El id de la venta no puede ser nulo");
        }

        if (idVenta <= 0) {
            log.error("El id de la venta debe ser mayor a cero");
            throw new NotFoundException("El id de la venta debe ser mayor a cero");
        }

        log.info("Recalculando total de venta con ID: {}", idVenta);

        VentasModel venta = ventasRepository.findById(idVenta)
                .orElseThrow(() -> {
                    log.error("No existe la venta con ID: {}", idVenta);
                    return new NotFoundException("No existe la venta con ID: " + idVenta);
                });

        List<DetalleVentaResponse> detalles = detalleVentaClient.obtenerDetallesPorVenta(idVenta);

        if (detalles == null || detalles.isEmpty()) {
            log.error("La venta no tiene detalles asociados");
            throw new NotFoundException("La venta no tiene detalles asociados");
        }

        log.info("Detalles obtenidos correctamente para venta ID: {}", idVenta);

        BigDecimal subTotal = detalles.stream()
                .map(DetalleVentaResponse::getSubTotal)
                .filter(sub -> sub != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = venta.getDescuentoTotal() != null
                ? venta.getDescuentoTotal()
                : BigDecimal.ZERO;

        if (descuento.compareTo(BigDecimal.ZERO) < 0) {
            log.error("El descuento no puede ser negativo");
            throw new NotFoundException("El descuento no puede ser negativo");
        }

        BigDecimal impuesto = subTotal.multiply(BigDecimal.valueOf(0.19));

        BigDecimal total = subTotal.subtract(descuento).add(impuesto);

        total = BigDecimal.valueOf(
                Math.round(total.doubleValue() / 10.0) * 10);

        BigDecimal montoPagado = venta.getMontoPagado() != null
                ? venta.getMontoPagado()
                : BigDecimal.ZERO;

        if (montoPagado.compareTo(total) < 0) {
            log.error("El monto pagado es insuficiente para la venta");
            throw new NotFoundException("El monto pagado es insuficiente para la venta");
        }

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

        if (id == null) {
            log.error("El id de la venta no puede ser nulo");
            throw new NotFoundException("El id de la venta no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id de la venta debe ser mayor a cero");
            throw new NotFoundException("El id de la venta debe ser mayor a cero");
        }

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

        if (venta == null) {
            log.error("La venta no puede ser nula");
            throw new NotFoundException("La venta no puede ser nula");
        }

        List<DetalleVentaResponse> detalles;

        try {
            detalles = detalleVentaClient.obtenerDetallesPorVenta(venta.getIdVenta());
        } catch (Exception e) {
            log.error("Error al obtener detalles de la venta con ID: {}", venta.getIdVenta());
            detalles = List.of();
        }

        ClienteResponse cliente = null;

        if (venta.getClienteId() != null) {
            try {
                cliente = clienteClient.obtenerClientePorId(venta.getClienteId());
            } catch (Exception e) {
                log.error("Error al obtener cliente con id: {}", venta.getClienteId());
            }
        }

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