package com.example.sdi2324entrega192.interceptors;

import com.example.sdi2324entrega192.services.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Clase para manejar el logout de usuarios.
 */
public class LogoutHandler implements LogoutSuccessHandler {
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LogService loggerService;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    Logger logger = LoggerFactory.getLogger(LogoutHandler.class);


    /**
     * Método invocado cuando se completa exitosamente el logout.
     * Registra la acción de logout en el servicio de logs, establece un mensaje de logout en la sesión
     * y redirige al usuario a la página de inicio de sesión.
     *
     * @param request La solicitud HTTP realizada.
     * @param response La respuesta HTTP a enviar.
     * @param authentication La información de autenticación del usuario.
     * @throws IOException Si ocurre un error de entrada/salida.
     * @throws ServletException Si ocurre un error durante la invocación del servlet.
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication != null && authentication.isAuthenticated()){
            // Registra el logout del usuario en el servicio de logs
            logger.info(
                    loggerService.logLogout(authentication.getName())
            );
            // Redirige al usuario a la página de inicio de sesión
            redirectStrategy.sendRedirect(request, response, "/login/error");
        }else{
            redirectStrategy.sendRedirect(request, response, "/login");
        }

    }
}
