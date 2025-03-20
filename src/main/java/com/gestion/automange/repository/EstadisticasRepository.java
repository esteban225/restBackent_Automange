package com.gestion.automange.repository;

import com.gestion.automange.model.Estadisticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadisticasRepository extends JpaRepository<Estadisticas, Long> {

    // 🔍 Obtiene la última estadística registrada correctamente
    Optional<Estadisticas> findFirstByOrderByIdDesc();
    
    // Alternativa con @Query en caso de problemas
    /*
    @Query("SELECT e FROM Estadisticas e ORDER BY e.id DESC LIMIT 1")
    Optional<Estadisticas> obtenerUltimaEstadistica();
    */
}
