package com.example.sdi2324entrega192.services;

import com.example.sdi2324entrega192.entities.Log;
import com.example.sdi2324entrega192.repositories.LogRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

/**
 * Clase de servicio para manejar los registros de actividad.
 */
@Service
public class LogService {

    /** Repositorio de registros de actividad */
    private final LogRepository logRepository;

    /**
     * Constructor de la clase LogService.
     *
     * @param logRepository El repositorio de registros de actividad.
     */
    public LogService(LogRepository logRepository){
        this.logRepository = logRepository;
    }

    /**
     * Elimina un registro de actividad por su ID.
     *
     * @param id El ID del registro de actividad a eliminar.
     */
    public void deleteLog(Long id){
        logRepository.deleteById(id);
    }

    /**
     * Obtiene todos los registros de actividad ordenados por fecha y hora de manera descendente.
     *
     * @return Una lista de todos los registros de actividad.
     */
    public List<Log> getLogs() {
        return logRepository.findAllByOrderByTimeDateDesc();
    }

    /**
     * Busca registros de actividad por tipo.
     *
     * @param searchText El texto de búsqueda para el tipo de registro.
     * @return Una lista de registros de actividad que coinciden con el tipo de búsqueda.
     */
    public List<Log> searchLogsByType(String searchText) {
        return logRepository.findAllByLogtype(searchText);
    }

    /**
     * Elimina todos los registros de actividad.
     */
    public void deleteLogs() {
        logRepository.deleteAll();
    }

    /**
     * Registra una petición.
     *
     * @param controller El controlador que maneja la petición.
     * @param httpMethod El método HTTP utilizado en la petición.
     * @param params Los parámetros de la petición.
     * @return La descripción de la petición registrada.
     */
    public String logRequest(String controller, String httpMethod, String... params){
        String description = "Petición del Controlador: " + controller + ". Método: " + httpMethod;
        if(params.length != 0){
            description += " .Parámetros:";
            for(String param : params){
                description += " " + param + " ";
            }
        }
        addLog(Log.LogTypes.PET, description);
        return description;
    }

    /**
     * Registra la creación de un usuario.
     *
     * @param controller El controlador que maneja la creación de usuario.
     * @param httpMethod El método HTTP utilizado en la creación de usuario.
     * @param params Los parámetros de la creación de usuario.
     * @return La descripción de la creación de usuario registrada.
     */
    public String logUserCreation(String controller, String httpMethod, String... params){
        String description = "Creación de usuario: " + controller + ". Método: " + httpMethod;
        if(params.length != 0){
            description += " .Parámetros:";
            for(String param : params){
                description += " " + param + " ";
            }
        }
        addLog(Log.LogTypes.ALTA, description);
        return description;
    }

    /**
     * Registra un inicio de sesión exitoso.
     *
     * @param username El nombre de usuario que ha iniciado sesión correctamente.
     * @return La descripción del inicio de sesión exitoso registrado.
     */
    public String logLoginSuccess(String username){
        String description = "Login correcto por el usuario: " + username;
        addLog(Log.LogTypes.LOGIN_EX, description);
        return description;
    }

    /**
     * Registra un intento fallido de inicio de sesión.
     *
     * @param username El nombre de usuario que ha intentado iniciar sesión sin éxito.
     * @return La descripción del intento fallido de inicio de sesión registrado.
     */
    public String logLoginFailure(String username){
        String description = "Intento de inicio de sesión fallido por el usuario: " + username;
        addLog(Log.LogTypes.LOGIN_ERR, description);
        return description;
    }

    /**
     * Registra un cierre de sesión.
     *
     * @param username El nombre de usuario que ha cerrado sesión.
     * @return La descripción del cierre de sesión registrado.
     */
    public String logLogout(String username){
        String description = "Cierre de sesión por el usuario: " + username;
        addLog(Log.LogTypes.LOGOUT, description);
        return description;
    }

    /**
     * Agrega un registro de actividad al repositorio.
     *
     * @param type El tipo de registro de actividad.
     * @param description La descripción del registro de actividad.
     */
    public void addLog(Log.LogTypes type, String description){
        Log log = new Log(type.toString(), new Timestamp(System.currentTimeMillis()), description);
        logRepository.save(log);
    }

    /**
     * Elimina registros de actividad por tipo.
     *
     * @param logType El tipo de registro de actividad a eliminar.
     */
    @Transactional
    public void deleteLogsByType(String logType) {
        logRepository.deleteByLogType(logType);
    }
}
