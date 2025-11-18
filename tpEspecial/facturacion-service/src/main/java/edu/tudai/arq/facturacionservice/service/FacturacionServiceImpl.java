package edu.tudai.arq.facturacionservice.service;

import edu.tudai.arq.facturacionservice.dto.FacturacionDTO;
import edu.tudai.arq.facturacionservice.entity.Facturacion;
import edu.tudai.arq.facturacionservice.entity.Tarifa;
import edu.tudai.arq.facturacionservice.entity.TipoTarifa;
import edu.tudai.arq.facturacionservice.exception.FacturacionNotFoundException;
import edu.tudai.arq.facturacionservice.exception.TarifaNotFoundException;
import edu.tudai.arq.facturacionservice.feignclient.CuentaFeignClient;
import edu.tudai.arq.facturacionservice.mapper.FacturacionMapper;
import edu.tudai.arq.facturacionservice.repository.FacturacionRepository;
import edu.tudai.arq.facturacionservice.repository.TarifaRepository;
import edu.tudai.arq.facturacionservice.service.interfaces.FacturacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturacionServiceImpl implements FacturacionService {

    private static final Logger logger = LoggerFactory.getLogger(FacturacionServiceImpl.class);

    private final FacturacionRepository facturacionRepo;
    private final TarifaRepository tarifaRepo;
    private final FacturacionMapper mapper;
    private final CuentaFeignClient cuentaClient;

    public FacturacionServiceImpl(FacturacionRepository facturacionRepo,
                                  TarifaRepository tarifaRepo,
                                  FacturacionMapper mapper,
                                  CuentaFeignClient cuentaClient) {
        this.facturacionRepo = facturacionRepo;
        this.tarifaRepo = tarifaRepo;
        this.mapper = mapper;
        this.cuentaClient = cuentaClient;
    }

    @Override
    @Transactional
    public FacturacionDTO.Response create(FacturacionDTO.Create in) {
        if (facturacionRepo.findByIdViaje(in.idViaje()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una facturación para el viaje con ID: " + in.idViaje());
        }

        LocalDate hoy = LocalDate.now();

        // Obtener tarifas vigentes
        Tarifa tarifaNormal = tarifaRepo.findTarifaVigente(TipoTarifa.NORMAL, hoy)
                .orElseThrow(() -> new TarifaNotFoundException("No hay tarifa NORMAL vigente"));

        Tarifa tarifaPausa = tarifaRepo.findTarifaVigente(TipoTarifa.PAUSA, hoy)
                .orElse(null);

        Tarifa tarifaExtendida = tarifaRepo.findTarifaVigente(TipoTarifa.PAUSA_EXTENDIDA, hoy)
                .orElse(null);

        // Tiempos del viaje
        Long tiempoNormal = in.tiempoSinPausas();
        Long tiempoPausaNormal = in.tiempoPausaNormal();
        Long tiempoPausaExtendida = in.tiempoPausaExtendida();

        // Calcular costos base
        double costoNormal = tiempoNormal * tarifaNormal.getPrecioPorMinuto();
        double costoPausa = (tarifaPausa != null && tiempoPausaNormal > 0)
                ? tiempoPausaNormal * tarifaPausa.getPrecioPorMinuto()
                : 0.0;
        double costoPausaExtendida = (tarifaExtendida != null && tiempoPausaExtendida > 0)
                ? tiempoPausaExtendida * tarifaExtendida.getPrecioPorMinuto()
                : 0.0;

        double montoTotalBase = costoNormal + costoPausa + costoPausaExtendida;

        // ==================== LÓGICA PREMIUM ====================
        Double montoTotal = montoTotalBase;
        boolean esPremium = false;
        Double kmGratisUsados = 0.0;

        try {
            // Obtener información de la cuenta
            var cuentaResponse = cuentaClient.getCuentaById(in.idCuenta());
            if (cuentaResponse.getBody() != null) {
                var cuenta = cuentaResponse.getBody();
                esPremium = "PREMIUM".equals(cuenta.tipoCuenta());

                if (esPremium && in.kilometrosRecorridos() != null && in.kilometrosRecorridos() > 0) {
                    // Usar kilómetros gratis si están disponibles
                    if (cuenta.kilometrosDisponibles() != null && cuenta.kilometrosDisponibles() > 0) {
                        var kmGratisResponse = cuentaClient.usarKilometrosGratis(
                                in.idCuenta(),
                                in.kilometrosRecorridos()
                        );

                        if (kmGratisResponse.getBody() != null) {
                            kmGratisUsados = kmGratisResponse.getBody();
                            Double proporcionGratis = kmGratisUsados / in.kilometrosRecorridos();

                            // Reducir el monto proporcionalmente a los km gratis
                            montoTotal *= (1 - proporcionGratis);

                            logger.info("Cuenta PREMIUM - Km gratis usados: {} de {} km. Descuento aplicado: {}%",
                                    kmGratisUsados, in.kilometrosRecorridos(), proporcionGratis * 100);
                        }
                    }

                    // Si es premium (con o sin km gratis), aplicar descuento 50% al resto
                    montoTotal *= 0.5;
                    logger.info("Cuenta PREMIUM - Descuento 50% aplicado. Monto final: ${}", montoTotal);
                }
            }
        } catch (Exception e) {
            logger.warn("No se pudo aplicar lógica premium para cuenta {}: {}", in.idCuenta(), e.getMessage());
            // Si falla, usar monto base sin descuentos
        }

        Long tiempoPausado = tiempoPausaNormal + tiempoPausaExtendida;

        Facturacion facturacion = mapper.toEntity(
                in.idViaje(),
                in.idCuenta(),
                in.tiempoTotal(),
                tiempoPausado,
                tarifaNormal.getId(),
                tarifaPausa != null ? tarifaPausa.getId() : null,
                tarifaExtendida != null ? tarifaExtendida.getId() : null,
                montoTotal
        );

        facturacion = facturacionRepo.save(facturacion);
        logger.info("Facturación creada - Viaje: {}, Cuenta: {}, Premium: {}, Km gratis: {}, Monto: ${}",
                in.idViaje(), in.idCuenta(), esPremium, kmGratisUsados, montoTotal);

        return mapper.toResponse(facturacion);
    }


    @Override
    @Transactional
    public void delete(Long id) {
        if (!facturacionRepo.existsById(id)) {
            throw new FacturacionNotFoundException("Facturación no encontrada con ID: " + id);
        }
        facturacionRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public FacturacionDTO.Response findById(Long id) {
        Facturacion facturacion = facturacionRepo.findById(id)
                .orElseThrow(() -> new FacturacionNotFoundException("Facturación no encontrada con ID: " + id));
        return mapper.toResponse(facturacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturacionDTO.Response> findAll() {
        return facturacionRepo.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FacturacionDTO.Response findByViaje(Long idViaje) {
        Facturacion facturacion = facturacionRepo.findByIdViaje(idViaje)
                .orElseThrow(() -> new FacturacionNotFoundException(
                        "No se encontró facturación para el viaje con ID: " + idViaje));
        return mapper.toResponse(facturacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturacionDTO.Response> findByCuenta(Long idCuenta) {
        return facturacionRepo.findByIdCuenta(idCuenta).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public Double getTotalFacturadoPorPeriodo(Integer anio, Integer mesDesde, Integer mesHasta) {
        LocalDateTime fechaInicio = LocalDateTime.of(anio, mesDesde, 1, 0, 0, 0);

        int ultimoDia;
        if (mesHasta == 2) {
            ultimoDia = (anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0)) ? 29 : 28;
        } else if (mesHasta == 4 || mesHasta == 6 || mesHasta == 9 || mesHasta == 11) {
            ultimoDia = 30;
        } else {
            ultimoDia = 31;
        }

        LocalDateTime fechaFin = LocalDateTime.of(anio, mesHasta, ultimoDia, 23, 59, 59);

        Double total = facturacionRepo.calcularTotalFacturado(fechaInicio, fechaFin);
        return total != null ? total : 0.0;
    }
}
