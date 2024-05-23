package com.example.sdi2324entrega192.controllers;

import com.example.sdi2324entrega192.entities.Log;
import com.example.sdi2324entrega192.services.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controlador para manejar las operaciones relacionadas con los registros de logs.
 */
@Controller
public class LogController {
    private final LogService logService;
    Logger logger = LoggerFactory.getLogger(LogController.class);

    /**
     * Constructor de la clase LogController.
     *
     * @param logService Servicio para gestionar los registros de logs.
     */
    public LogController(LogService logService){
        this.logService = logService;
    }

    /**
     * Método para obtener la lista de logs.
     *
     * @param model   El modelo que se utilizará para pasar datos a la vista.
     * @param logType El tipo de log a filtrar (opcional).
     * @return El nombre de la vista a la que se redireccionará el usuario.
     */
    @RequestMapping(value = "/logs/list", method = RequestMethod.GET)
    public String getList(Model model, @RequestParam(value = "", required = false) String logType) {
        try {
            // Obtener la lista de logs
            List<Log> logs;
            if (logType != null) {
                logs = logService.searchLogsByType(logType);
            } else {
                logs = logService.getLogs();
            }
            // Agregar la lista de logs al modelo para ser mostrada en la vista
            model.addAttribute("logsList", logs);

            // Registrar la solicitud en los logs
            logger.info(
                    logService.logRequest("LogController --> /logs/list", "GET", new String[]{"logType=" + logType})
            );

            return "logs/list";
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir durante la obtención de la lista de logs
            logger.error("Error al obtener la lista de logs: " + e.getMessage());
            return "redirect:/error";
        }
    }
    /**
     * Método para actualizar la lista de logs.
     *
     * @param model   El modelo que se utilizará para pasar datos a la vista.
     * @param logType El tipo de log a filtrar (opcional).
     * @return El nombre de la vista a la que se redireccionará el usuario.
     */
    @RequestMapping("/logs/list/update")
    public String update(Model model, @RequestParam(value = "", required = false) String logType) {
        try {
            // Obtener la lista de logs actualizada
            List<Log> logs;
            if (logType != null && !logType.equals("ALL")) {
                logs = logService.searchLogsByType(logType);
            } else {
                logs = logService.getLogs();
            }

            // Agregar la lista de logs al modelo para ser mostrada en la vista
            model.addAttribute("logsList", logs);

            // Registrar la solicitud en los logs
            logger.info(
                    logService.logRequest("LogController --> /logs/list/update", "GET", new String[]{"logType=" + logType})
            );

            return "logs/list :: tableLogs";
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir durante la actualización de la lista de logs
            logger.error("Error al actualizar la lista de logs: " + e.getMessage());
            return "redirect:/error";
        }
    }
    /**
     * Método para eliminar un registro de log por su ID.
     *
     * @param model   El modelo que se utilizará para pasar datos a la vista.
     * @param logType El tipo de log a filtrar (opcional).
     * @param id      El ID del registro de log que se desea eliminar.
     * @return El nombre de la vista a la que se redireccionará el usuario.
     */
    @RequestMapping("/log/delete/{id}")
    public String delete(Model model, @RequestParam(value = "", required = false) String logType, @PathVariable Long id) {
        try {
            // Eliminar el registro de log utilizando el servicio correspondiente
            logService.deleteLog(id);

            // Registrar la eliminación en los logs
            logger.info(
                    logService.logRequest("LogController --> /log/delete/" + id, "POST", new String[]{"Id:" + id.toString()})
            );

            // Actualizar la lista de logs y redirigir al usuario a la vista correspondiente
            return update(model, logType);
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir durante la eliminación del registro de log
            logger.error("Error al eliminar el registro de log: " + e.getMessage());
            return "redirect:/error";
        }
    }

    /**
     * Método para eliminar los registros de logs por tipo.
     *
     * @param model   El modelo que se utilizará para pasar datos a la vista.
     * @param logType El tipo de log a filtrar.
     * @return El nombre de la vista a la que se redireccionará el usuario.
     */
    @RequestMapping(value = "/logs/delete")
    public String deleteLogsByTyoe(Model model, @RequestParam("logType") String logType) {
        try {
            // Verificar si se deben eliminar todos los logs o solo los de un tipo específico
            if (logType.equals("ALL")) {
                logService.deleteLogs();
            } else {
                logService.deleteLogsByType(logType);
            }

            // Registrar la eliminación en los logs
            logger.info(
                    logService.logRequest("LogController --> /logs/delete?logType=" + logType, "POST", new String[]{"logType:" + logType})
            );

            // Actualizar la lista de logs y redirigir al usuario a la vista correspondiente
            return update(model, logType);
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir durante la eliminación de los registros de logs
            logger.error("Error al eliminar los registros de logs: " + e.getMessage());
            return "redirect:/error";
        }
    }
}
