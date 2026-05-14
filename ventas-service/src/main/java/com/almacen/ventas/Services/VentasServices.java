package com.almacen.ventas.Services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

@Service
public class VentasServices {

    private final VentasRepository ventasRepository;
    private final ClienteClient clienteClient;
    private final DetalleVentaClient detalleVentaClient;

    public VentasServices(
            VentasRepository ventasRepository,
            ClienteClient clienteClient,
            DetalleVentaClient detalleVentaClient
    ) {
        this.ventasRepository = ventasRepository;
        this.clienteClient = clienteClient;
        this.detalleVentaClient = detalleVentaClient;
    }

    public List<VentasResponse> obtenerTodas() {
        return ventasRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VentasResponse obtenerPorId(Long id) {
        return ventasRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("No existe la venta con ID: " + id));
    }

    public VentasResponse guardar(VentasRequest request) {

        clienteClient.validarCliente(request.getClienteID());

        VentasModel venta = VentasModel.builder()
                .fechaVenta(LocalDateTime.now())
                .subTotal(request.getSubTotal())
                .descuentoTotal(request.getDescuentoTotal())
                .impuestoTotal(request.getImpuestoTotal())
                .total(request.getTotal())
                .metodoPago(request.getMetodoPago())
                .tipoComprobante(request.getTipoComprobante())
                .montoPagado(request.getMontoPagado())
                .vuelto(request.getVuelto())
                .estadoVenta(EstadoVentas.COMPLETADA)
                .clienteID(request.getClienteID())
                .empleadoId(request.getEmpleadoId())
                .numeroComprobante(request.getNumeroComprobante())
                .observaciones(request.getObservaciones())
                .build();

        return toResponse(ventasRepository.save(venta));
    }

    public VentasResponse actualizar(Long id, VentasRequest request) {
        VentasModel venta = ventasRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la venta con ID: " + id));

        clienteClient.validarCliente(request.getClienteID());

        venta.setSubTotal(request.getSubTotal());
        venta.setDescuentoTotal(request.getDescuentoTotal());
        venta.setImpuestoTotal(request.getImpuestoTotal());
        venta.setTotal(request.getTotal());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setTipoComprobante(request.getTipoComprobante());
        venta.setMontoPagado(request.getMontoPagado());
        venta.setVuelto(request.getVuelto());
        venta.setClienteID(request.getClienteID());
        venta.setEmpleadoId(request.getEmpleadoId());
        venta.setNumeroComprobante(request.getNumeroComprobante());
        venta.setObservaciones(request.getObservaciones());

        return toResponse(ventasRepository.save(venta));
    }

    public VentasResponse recalcularTotal(Long idVenta) {
        VentasModel venta = ventasRepository.findById(idVenta)
                .orElseThrow(() -> new NotFoundException("No existe la venta con ID: " + idVenta));

        List<DetalleVentaResponse> detalles = detalleVentaClient.obtenerDetallesPorVenta(idVenta);

        BigDecimal subTotal = detalles.stream()
                .map(DetalleVentaResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = venta.getDescuentoTotal() != null
                ? venta.getDescuentoTotal()
                : BigDecimal.ZERO;

        BigDecimal impuesto = subTotal.multiply(BigDecimal.valueOf(0.19));

        BigDecimal total = subTotal.subtract(descuento).add(impuesto);

        venta.setSubTotal(subTotal);
        venta.setImpuestoTotal(impuesto);
        venta.setTotal(total);

        return toResponse(ventasRepository.save(venta));
    }

    public void eliminar(Long id) {
        VentasModel venta = ventasRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la venta con ID: " + id));

        ventasRepository.delete(venta);
    }

    private VentasResponse toResponse(VentasModel venta) {
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
                .clienteID(venta.getClienteID())
                .empleadoId(venta.getEmpleadoId())
                .numeroComprobante(venta.getNumeroComprobante())
                .observaciones(venta.getObservaciones())
                .build();
    }
}