package com.example.sdi2324entrega192.pageobjects;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase para manejar archivos de propiedades y obtener cadenas de texto localizadas.
 */
public class PO_Properties {
    static private String Path;
    static final int SPANISH = 0;
    static final int ENGLISH = 1;
    static final int FRENCH = 2;
    static final Locale[] idioms = new Locale[] {new Locale("ES"), new Locale("EN"),new Locale("FR")};

    /**
     * Constructor de la clase PO_Properties.
     *
     * @param Path La ruta del archivo de propiedades.
     */
    public PO_Properties(String Path) {
        PO_Properties.Path = Path;
    }

    /**
     * Método para obtener una cadena de texto localizada en el archivo de propiedades.
     *
     * @param prop   La clave de la propiedad.
     * @param locale El índice del idioma (0 para español, 1 para inglés).
     * @return La cadena de texto localizada.
     */
    public String getString(String prop, int locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(Path, idioms[locale]);
        String value = bundle.getString(prop);
        String result;
        result = new String(value.getBytes(StandardCharsets.UTF_8),  StandardCharsets.UTF_8);
        return result;
    }

    /**
     * Método estático para obtener el índice del idioma español.
     *
     * @return El índice del idioma español.
     */
    public static int getSPANISH() {
        return SPANISH;
    }

    /**
     * Método estático para obtener el índice del idioma inglés.
     *
     * @return El índice del idioma inglés.
     */
    public static int getENGLISH() {
        return ENGLISH;
    }
    /**
     * Método estático para obtener el índice del idioma inglés.
     *
     * @return El índice del idioma inglés.
     */
    public static int getFRENCH() {
        return FRENCH;
    }

}
