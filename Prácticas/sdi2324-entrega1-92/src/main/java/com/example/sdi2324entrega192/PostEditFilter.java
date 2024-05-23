package com.example.sdi2324entrega192;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro para editar publicaciones.
 */
public class PostEditFilter extends OncePerRequestFilter {

    /**
     * Filtra las solicitudes HTTP para editar publicaciones.
     *
     * @param request  La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @param filterChain El filtro de cadena.
     * @throws ServletException Si ocurre una excepción de servlet.
     * @throws IOException      Si ocurre una excepción de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && isPostEditUrl(request)) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                filterChain.doFilter(request, response);
            } else {
                SecurityContextHolder.clearContext();
                response.sendRedirect("/login"); // Reemplaza "/login" con tu URL de inicio de sesión
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Verifica si la URL de la solicitud es para editar una publicación.
     *
     * @param request La solicitud HTTP.
     * @return true si la URL es para editar una publicación, false de lo contrario.
     */
    private boolean isPostEditUrl(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/post/edit/");
    }
}
