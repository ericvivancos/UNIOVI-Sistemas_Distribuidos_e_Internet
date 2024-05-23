package com.example.sdi2324entrega192.interceptors;

import com.example.sdi2324entrega192.services.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Clase que maneja el éxito en la autenticación del usuario.
 */
public class CustomSuccessHandler implements AuthenticationSuccessHandler {



    @Autowired
    private LogService loggerService;

    // Estrategia de redireccionamiento predeterminada
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);

    /**
     * Método llamado cuando la autenticación es exitosa. Registra el inicio de sesión exitoso y redirige al usuario a la página de inicio.
     *
     * @param request        El objeto HttpServletRequest que representa la solicitud HTTP.
     * @param response       El objeto HttpServletResponse que representa la respuesta HTTP.
     * @param authentication La instancia de Authentication que representa la información de autenticación del usuario.
     * @throws ServletException Excepción que indica un error durante el manejo de la solicitud.
     * @throws IOException      Excepción que indica un error de E/S.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {
        // Registra el inicio de sesión exitoso
        logger.info(loggerService.logLoginSuccess(authentication.getName()));

        // Redirige al usuario a la página de inicio
        redirectStrategy.sendRedirect(request, response, "/home");
    }

}
