package com.example.sdi2324entrega192.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Clase de servicio para manejar la seguridad.
 */
@Service
public class SecurityService {

    /** Logger para la clase SecurityService */
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    /** Administrador de autenticación */
    private final AuthenticationManager authenticationManager;

    /** Detalles del usuario */
    private final UserDetailsService userDetailsService;

    /**
     * Constructor de la clase SecurityService.
     *
     * @param authenticationManager El administrador de autenticación.
     * @param userDetailsService Los detalles del usuario.
     */
    public SecurityService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService){
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Encuentra el DNI del usuario autenticado.
     *
     * @return El DNI del usuario autenticado, o null si no se encuentra.
     */
    public String findLoggedInDni(){
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if(userDetails instanceof UserDetails){
            return ((UserDetails)userDetails).getUsername();
        }
        return null;
    }

    /**
     * Inicia sesión automáticamente.
     *
     * @param dni El DNI del usuario.
     * @param password La contraseña del usuario.
     */
    public void autoLogin(String dni, String password){
        UserDetails userDetails = userDetailsService.loadUserByUsername(dni);

        UsernamePasswordAuthenticationToken aToken = new UsernamePasswordAuthenticationToken(
                userDetails, password, userDetails.getAuthorities()
        );
        authenticationManager.authenticate(aToken);
        if(aToken.isAuthenticated()){
            SecurityContextHolder.getContext().setAuthentication(aToken);
            logger.debug(String.format("Inicio de sesión automático para %s exitoso!", dni));
        }
    }
}
