package edu.tudai.arq.adminservice.service;

import edu.tudai.arq.adminservice.dto.ReporteOperacionDTO;
import edu.tudai.arq.adminservice.dto.TarifaDTO;
import edu.tudai.arq.adminservice.dto.TotalFacturadoDTO;
import edu.tudai.arq.adminservice.feignclient.*;
import edu.tudai.arq.adminservice.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class AdminService {

    private final UsuarioFeignClient usuarioClient;
    private final MonopatinFeignClient monopatinClient;
    private final ParadaFeignClient paradaClient;
    private final ViajeFeignClient viajeClient;
    private final FacturacionFeignClient facturacionClient;
    private final MantenimientoFeignClient mantenimientoClient;

    public AdminService(UsuarioFeignClient usuarioClient,
                        MonopatinFeignClient monopatinClient,
                        ParadaFeignClient paradaClient,
                        ViajeFeignClient viajeClient,
                        FacturacionFeignClient facturacionClient,
                        MantenimientoFeignClient mantenimientoClient) {
        this.usuarioClient = usuarioClient;
        this.monopatinClient = monopatinClient;
        this.paradaClient = paradaClient;
        this.viajeClient = viajeClient;
        this.facturacionClient = facturacionClient;
        this.mantenimientoClient = mantenimientoClient;
    }

    // b. Anular cuenta
    public void anularCuenta(Long idCuenta) {
        usuarioClient.anularCuenta(idCuenta);
    }

    // Habilitar cuenta
    public void habilitarCuenta(Long idCuenta) {
        usuarioClient.habilitarCuenta(idCuenta);
    }

    // c. Monopatines con más de X viajes en un año
    public List<MonopatinModel> getMonopatinesConMasDeXViajes(Integer cantidadViajes, Integer anio) {
        return monopatinClient.getMonopatinesConMasDeXViajes(cantidadViajes, anio).getBody();
    }

    // d. Total facturado en rango de meses
    public TotalFacturadoDTO getTotalFacturadoEnRango(Integer anio, Integer mesDesde, Integer mesHasta) {
        // Construir fechas de inicio y fin
        YearMonth yearMonthDesde = YearMonth.of(anio, mesDesde);
        YearMonth yearMonthHasta = YearMonth.of(anio, mesHasta);

        LocalDate fechaDesde = yearMonthDesde.atDay(1);
        LocalDate fechaHasta = yearMonthHasta.atEndOfMonth();

        String fechaDesdeStr = fechaDesde.atStartOfDay().toString();
        String fechaHastaStr = fechaHasta.atTime(23, 59, 59).toString();

        Double total = facturacionClient.getTotalFacturado(fechaDesdeStr, fechaHastaStr).getBody();

        return new TotalFacturadoDTO(
                total != null ? total : 0.0,
                fechaDesdeStr,
                fechaHastaStr,
                "ARS"
        );
    }

    // e. Monopatines en operación vs en mantenimiento
    public ReporteOperacionDTO getReporteOperacion() {
        List<MonopatinModel> enOperacion = monopatinClient.getMonopatinesByEstado("DISPONIBLE").getBody();
        List<MonopatinModel> enUso = monopatinClient.getMonopatinesByEstado("EN_USO").getBody();
        List<MonopatinModel> enMantenimiento = monopatinClient.getMonopatinesByEstado("EN_MANTENIMIENTO").getBody();

        long totalEnOperacion = (enOperacion != null ? enOperacion.size() : 0) +
                                (enUso != null ? enUso.size() : 0);
        long totalEnMantenimiento = enMantenimiento != null ? enMantenimiento.size() : 0;
        long totalMonopatines = totalEnOperacion + totalEnMantenimiento;

        double porcentajeOperacion = totalMonopatines > 0 ?
                (totalEnOperacion * 100.0 / totalMonopatines) : 0.0;
        double porcentajeMantenimiento = totalMonopatines > 0 ?
                (totalEnMantenimiento * 100.0 / totalMonopatines) : 0.0;

        return new ReporteOperacionDTO(
                totalEnOperacion,
                totalEnMantenimiento,
                porcentajeOperacion,
                porcentajeMantenimiento
        );
    }

    // f. Ajuste de precios
    public TarifaModel ajustarPrecio(TarifaDTO.CreateUpdate tarifaDTO) {
        TarifaModel tarifa = new TarifaModel();
        tarifa.setTipoTarifa(tarifaDTO.tipoTarifa());
        tarifa.setPrecioPorMinuto(tarifaDTO.precioPorMinuto());
        tarifa.setFechaVigenciaDesde(tarifaDTO.fechaVigenciaDesde());
        tarifa.setActiva(true);

        return facturacionClient.createTarifa(tarifa).getBody();
    }

    // Gestión de monopatines
    public List<MonopatinModel> getAllMonopatines() {
        return monopatinClient.getAllMonopatines().getBody();
    }

    public MonopatinModel getMonopatinById(Long id) {
        return monopatinClient.getMonopatinById(id).getBody();
    }

    public MonopatinModel createMonopatin(MonopatinModel monopatin) {
        return monopatinClient.createMonopatin(monopatin).getBody();
    }

    public void deleteMonopatin(Long id) {
        monopatinClient.deleteMonopatin(id);
    }

    // Gestión de paradas
    public List<ParadaModel> getAllParadas() {
        return paradaClient.getAllParadas().getBody();
    }

    public ParadaModel getParadaById(Long id) {
        return paradaClient.getParadaById(id).getBody();
    }

    public ParadaModel createParada(ParadaModel parada) {
        return paradaClient.createParada(parada).getBody();
    }

    public void deleteParada(Long id) {
        paradaClient.deleteParada(id);
    }

    public ParadaModel updateParada(Long id, ParadaModel parada) {
        return paradaClient.updateParada(id, parada).getBody();
    }

    // Gestión de tarifas
    public List<TarifaModel> getAllTarifas() {
        return facturacionClient.getAllTarifas().getBody();
    }

    public List<TarifaModel> getTarifasActivas() {
        return facturacionClient.getTarifasActivas().getBody();
    }

    public void desactivarTarifa(Long id, String fechaFin) {
        facturacionClient.desactivarTarifa(id, fechaFin);
    }

    // Gestión de mantenimientos
    public List<MantenimientoModel> getAllMantenimientos() {
        return mantenimientoClient.getAllMantenimientos().getBody();
    }

    public List<MantenimientoModel> getMantenimientosActivos() {
        return mantenimientoClient.getMantenimientosActivos().getBody();
    }

    public MantenimientoModel registrarMantenimiento(MantenimientoModel mantenimiento) {
        return mantenimientoClient.registrarMantenimiento(mantenimiento).getBody();
    }

    public MantenimientoModel finalizarMantenimiento(Long id) {
        return mantenimientoClient.finalizarMantenimiento(id).getBody();
    }

    // Gestión de cuentas
    public List<CuentaModel> getAllCuentas() {
        return usuarioClient.getAllCuentas().getBody();
    }

    public CuentaModel getCuentaById(Long id) {
        return usuarioClient.getCuentaById(id).getBody();
    }
}

