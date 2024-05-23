package com.example.sdi2324entrega192.controllers;

import com.example.sdi2324entrega192.services.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

/**
 * Controlador para manejar errores genéricos de la aplicación.
 */
@Controller
public class GenericErrorController implements ErrorController {
    @Autowired
    private LogService logService;
    Logger logger = LoggerFactory.getLogger(FriendsController.class);
    /**
     * Método para manejar las solicitudes de error y redirigir a la página de acceso denegado.
     *
     * @return El nombre de la vista a la que se redireccionará en caso de error.
     */
    @RequestMapping("/error")
    public String Error(){

        logger.info(
                logService.logRequest("GenericErrorController --> /error","GET",new String[]{})
        );
        return "accessDenied";
    }
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException() {
        // Maneja la excepción de acceso denegado redirigiendo a la página de error
        return "redirect:/accessDenied";
    }
}
