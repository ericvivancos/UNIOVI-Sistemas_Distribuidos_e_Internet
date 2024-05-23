package com.example.sdi2324entrega192.services;

import org.springframework.stereotype.Service;

/**
 * Clase de servicio para manejar los roles de usuario.
 */
@Service
public class RolesService {

    /** Roles de usuario */
    String[] roles = {"ROLE_USER", "ROLE_ADMIN"};

    /** Constantes para los roles de usuario */
    public static final int USER = 0;
    public static final int ADMIN = 1;

    /**
     * Obtiene todos los roles de usuario.
     *
     * @return Un arreglo de strings con los roles de usuario.
     */
    public String[] getRoles() {
        return roles;
    }
}

