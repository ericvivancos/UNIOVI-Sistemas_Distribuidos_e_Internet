package com.example.sdi2324entrega192.repositories;

import com.example.sdi2324entrega192.entities.Log;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Interfaz para el repositorio de registros de actividad.
 */
public interface LogRepository extends CrudRepository<Log, Long> {

    /**
     * Encuentra todos los registros de actividad ordenados por fecha y hora de manera descendente.
     *
     * @return Una lista de registros de actividad.
     */
    List<Log> findAllByOrderByTimeDateDesc();

    /**
     * Encuentra todos los registros de actividad filtrados por tipo de registro, ordenados por fecha y hora de manera descendente.
     *
     * @param logType El tipo de registro.
     * @return Una lista de registros de actividad filtrados por tipo.
     */
    @Query("SELECT l FROM Log l WHERE l.logType = (?1) ORDER BY l.timeDate DESC")
    List<Log> findAllByLogtype(String logType);

    /**
     * Elimina todos los registros de actividad de un tipo específico.
     *
     * @param logType El tipo de registro a eliminar.
     */
    void deleteByLogType(String logType);
}
