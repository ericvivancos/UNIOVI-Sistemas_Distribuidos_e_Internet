package com.example.sdi2324entrega192.interceptors;

import com.example.sdi2324entrega192.services.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Clase que maneja el fallo en la autenticación del usuario.
 */
public class LoginFailure implements AuthenticationFailureHandler {
    @Autowired
    private LogService logService;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    Logger logger = LoggerFactory.getLogger(LoginFailure.class);

    /**
     * Método invocado cuando falla el proceso de autenticación.
     * Registra el fallo de inicio de sesión y redirige al usuario a la página de inicio de sesión.
     *
     * @param request
     * @param response
     * @param exception
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // Registra el fallo de inicio de sesión
        logger.info(logService.logLoginFailure(request.getParameter("username")));

        // Almacena la excepción de autenticación en la sesión
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);

        // Redirige al usuario a la página de inicio de sesión
        redirectStrategy.sendRedirect(request, response, "/login");
    }
}
