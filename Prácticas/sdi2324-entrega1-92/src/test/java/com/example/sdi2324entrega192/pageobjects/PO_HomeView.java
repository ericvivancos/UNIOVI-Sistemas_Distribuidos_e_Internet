package com.example.sdi2324entrega192.pageobjects;

import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Clase ara interactuar con la vista de la página de inicio (HomeView) en la aplicación.
 */
public class PO_HomeView extends PO_NavView {

    /**
     * Comprueba si se muestra el mensaje de bienvenida en el idioma especificado.
     * @param driver El controlador WebDriver utilizado para la interacción con el navegador.
     * @param language El código del idioma (localización) para el cual se espera el mensaje de bienvenida.
     */
    static public void checkWelcomeToPage(WebDriver driver, int language) {
        // Esperamos a que se cargue el saludo de bienvenida en el idioma especificado
        SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("welcome.message", language),
                getTimeout());
    }

    /**
     * Obtiene el elemento que contiene el mensaje de bienvenida en el idioma especificado.
     * @param driver El controlador WebDriver utilizado para la interacción con el navegador.
     * @param language El código del idioma (localización) para el cual se espera el mensaje de bienvenida.
     * @return Una lista de elementos web que contienen el mensaje de bienvenida.
     */
    static public List<WebElement> getWelcomeMessageText(WebDriver driver, int language) {
        // Esperamos a que se cargue el saludo de bienvenida en el idioma especificado
        return SeleniumUtils.waitLoadElementsBy(driver, "text", p.getString("welcome.message", language),
                getTimeout());
    }

    /**
     * Comprueba si el cambio de idioma se refleja en el mensaje de bienvenida.
     * @param driver El controlador WebDriver utilizado para la interacción con el navegador.
     * @param textLanguage1 El texto del primer idioma.
     * @param textLanguage El texto del segundo idioma.
     * @param locale1 El código de localización del primer idioma.
     * @param locale2 El código de localización del segundo idioma.
     */
    static public void checkChangeLanguage(WebDriver driver, String textLanguage1, String textLanguage,
                                           int locale1, int locale2) {
        // Esperamos a que se cargue el saludo de bienvenida en el primer idioma
        PO_HomeView.checkWelcomeToPage(driver, locale1);
        // Cambiamos al segundo idioma
        PO_HomeView.changeLanguage(driver, textLanguage);
        // Comprobamos que el texto de bienvenida haya cambiado al segundo idioma
        PO_HomeView.checkWelcomeToPage(driver, locale2);
        // Volvemos al primer idioma
        PO_HomeView.changeLanguage(driver, textLanguage1);
        // Esperamos a que se cargue el saludo de bienvenida en el primer idioma
        PO_HomeView.checkWelcomeToPage(driver, locale1);
    }
}