package com.example.sdi2324entrega192.controllers;

import com.example.sdi2324entrega192.services.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para manejar las solicitudes relacionadas con la página de inicio de la aplicación.
 */
@Controller
public class HomeController {
    @Autowired
    private LogService logService;
    Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Método para manejar las solicitudes a la página de inicio ("/").
     *
     * @return El nombre de la vista a la que se redireccionará el usuario.
     */
    @RequestMapping("/")
    public String index(){
        logger.info(
                logService.logRequest("HomeController --> /","GET",new String[]{})
        );
        return "index";
    }
}
