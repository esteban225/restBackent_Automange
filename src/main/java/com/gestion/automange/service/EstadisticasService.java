package com.gestion.automange.service;

import com.gestion.automange.model.Estadisticas;
import com.gestion.automange.repository.EstadisticasRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class EstadisticasService {

    private static final Logger logger = LoggerFactory.getLogger(EstadisticasService.class);

    private final JdbcTemplate jdbcTemplate;
    private final EstadisticasRepository estadisticasRepository;

    public EstadisticasService(JdbcTemplate jdbcTemplate, EstadisticasRepository estadisticasRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.estadisticasRepository = estadisticasRepository;
    }

    // 🕒 Ejecutar cada 5 segundos
    @Scheduled(fixedRate = 5000)
    public void tareaProgramadaActualizarEstadisticas() {
        logger.info("⏰ Ejecutando tarea programada para actualizar estadísticas...");
        actualizarEstadisticas();
    }

    public void actualizarEstadisticas() {
        try {
            logger.info("🔍 Contando estadísticas actuales...");

            int usuariosRegistrados = contarUsuarios();
            int vehiculosRevisados = contarVehiculos();
            int inventario = contarInventario();
            int ventas = contarOrdenes();

            logger.info("📊 Resultados obtenidos -> Usuarios: {}, Vehículos: {}, Inventario: {}, Ventas: {}",
                        usuariosRegistrados, vehiculosRevisados, inventario, ventas);

            Optional<Estadisticas> ultimaEstadisticaOpt = estadisticasRepository.findFirstByOrderByIdDesc();
            if (ultimaEstadisticaOpt.isPresent()) {
                Estadisticas ultimaEstadistica = ultimaEstadisticaOpt.get();
                logger.info("📌 Última estadística registrada -> Usuarios: {}, Vehículos: {}, Inventario: {}, Ventas: {}",
                            ultimaEstadistica.getUsuariosRegistrados(),
                            ultimaEstadistica.getVehiculosRevisados(),
                            ultimaEstadistica.getInventario(),
                            ultimaEstadistica.getVentas());

                if (ultimaEstadistica.getUsuariosRegistrados() == usuariosRegistrados
                        && ultimaEstadistica.getVehiculosRevisados() == vehiculosRevisados
                        && ultimaEstadistica.getInventario() == inventario
                        && ultimaEstadistica.getVentas() == ventas) {
                    logger.info("✅ No hay cambios en las estadísticas. No se actualizará la tabla.");
                    return;
                }
            } else {
                logger.info("📌 No hay estadísticas anteriores registradas.");
            }

            Estadisticas nuevaEstadistica = new Estadisticas();
            nuevaEstadistica.setUsuariosRegistrados(usuariosRegistrados);
            nuevaEstadistica.setVehiculosRevisados(vehiculosRevisados);
            nuevaEstadistica.setInventario(inventario);
            nuevaEstadistica.setVentas(ventas);

            estadisticasRepository.save(nuevaEstadistica);
            logger.info("✅ Estadísticas guardadas correctamente en la base de datos.");

        } catch (Exception e) {
            logger.error("❌ Error al actualizar estadísticas: ", e);
        }
    }

    private int contarUsuarios() {
        return ejecutarConsulta("SELECT COUNT(*) FROM usuarios", "usuarios");
    }

    private int contarVehiculos() {
        return ejecutarConsulta("SELECT COUNT(*) FROM vehiculos", "vehículos");
    }

    private int contarInventario() {
        return ejecutarConsulta("SELECT COALESCE(SUM(cantidad), 0) FROM productos", "inventario");
    }

    private int contarOrdenes() {
        return ejecutarConsulta("SELECT COUNT(*) FROM ordenes", "órdenes");
    }

    private int ejecutarConsulta(String sql, String entidad) {
        try {
            logger.debug("📥 Ejecutando consulta para {}: {}", entidad, sql);
            int resultado = jdbcTemplate.queryForObject(sql, Integer.class);
            logger.debug("📤 Resultado de {}: {}", entidad, resultado);
            return resultado;
        } catch (Exception e) {
            logger.error("❌ Error al contar {}: ", entidad, e);
            return 0;
        }
    }
}
