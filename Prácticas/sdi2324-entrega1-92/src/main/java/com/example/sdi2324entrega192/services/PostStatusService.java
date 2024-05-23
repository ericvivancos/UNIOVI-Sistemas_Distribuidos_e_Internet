package com.example.sdi2324entrega192.services;

import org.springframework.stereotype.Service;

/**
 * Clase de servicio para manejar los estados de las publicaciones.
 */
@Service
public class PostStatusService {

    /** Estados de las publicaciones */
    static String[] status = {"Aceptada", "Moderada", "Censurada"};

    /** Constantes para los estados de las publicaciones */
    public static final int Aceptada = 0;
    public static final int Moderada = 1;
    public static final int Censurada = 2;

    /**
     * Obtiene todos los estados de las publicaciones.
     *
     * @return Un arreglo de strings con los estados de las publicaciones.
     */
    public String[] getStatus() {
        return status;
    }

    /**
     * Obtiene el estado de una publicación según el índice dado.
     *
     * @param i El índice del estado.
     * @return El estado de la publicación correspondiente al índice.
     */
    public static String getStatus(int i){
        return status[i];
    }
}
