package com.example.sdi2324entrega192.interceptors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * Interceptor para controlar que una vez se salga de la página /user/list se borre de la sesión el atributo
 * que marca el criterio de búsqueda en la lista de usuarios.
 */
@Component
public class ClearSearchInterceptor implements HandlerInterceptor {


    /**
     * Método que se ejecuta después de manejar la solicitud. Elimina el atributo de sesión que indica
     * el criterio de búsqueda si el usuario no está en la página /user/list.
     *
     * @param request        El objeto HttpServletRequest que representa la solicitud HTTP.
     * @param response       El objeto HttpServletResponse que representa la respuesta HTTP.
     * @param handler        El controlador que maneja la solicitud.
     * @param modelAndView  El objeto ModelAndView que representa el modelo y la vista.
     * @throws Exception     Excepción que indica un error durante el manejo de la solicitud.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Obtiene la URL completa de la solicitud
        String fullUrl = request.getRequestURL().toString();


        if (!fullUrl.contains("/user/list") && modelAndView!=null) {
            // Si el usuario no está en la página user/list, elimina el atributo de sesión si estaba presente
            request.getSession().removeAttribute("searchText");
        }

    }


}
