package com.example.sdi2324entrega192.pageobjects;

import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;


/**
 * Clase para interactuar con la barra de navegación (navbar).
 */
public class PO_NavBar {
    protected static int timeout = 4;

    /**
     * Método estático que devuelve el tiempo de espera predeterminado.
     * @return El tiempo de espera predeterminado en segundos.
     */
    public static int getTimeout() {
        return timeout;
    }

    /**
     * Método privado estático para hacer clic en un elemento identificado por su ID.
     * @param driver El controlador WebDriver para interactuar con la página web.
     * @param elementId El ID del elemento en el que se hará clic.
     */
    private static void clickElementById(WebDriver driver, String elementId) {
        // Esperamos a que el elemento con el ID dado esté presente en la página.
        WebElement element = SeleniumUtils.waitLoadElementById(driver, elementId, getTimeout());

        // Verificamos que el elemento esté presente.
        Assertions.assertNotNull(element, "Elemento con ID '" + elementId + "' no encontrado en la página.");

        // Hacemos clic en el elemento.
        element.click();
    }

    /**
     * Método estático para hacer clic en una de las opciones principales del navbar y verificar la navegación.
     * @param driver El controlador WebDriver para interactuar con la página web.
     * @param textOption El texto de la opción principal en el navbar.
     * @param criterio El criterio de búsqueda para el elemento de destino ("id", "class", "text", "@attribute" o "free").
     * @param targetText El texto correspondiente a la búsqueda del elemento en la página destino.
     */
    public static void clickOption(WebDriver driver, String textOption, String criterio, String targetText) {
        //CLickamos en la opción del navbar y esperamos a que se cargue el enlace.
        List<WebElement> elements = SeleniumUtils.waitLoadElementsBy(driver, "@href", textOption,
                getTimeout());
        //Tiene que haber un sólo elemento.
        Assertions.assertEquals(1, elements.size());
        //Ahora lo clickamos
        elements.get(0).click();
        //Esperamos a que sea visible un elemento concreto
        elements = SeleniumUtils.waitLoadElementsBy(driver, criterio, targetText, getTimeout());
        //Tiene que haber un sólo elemento.
        Assertions.assertEquals(1, elements.size());
    }

    /**
     * Método estático para ir a la página de inicio de sesión.
     * @param driver El controlador WebDriver para interactuar con la página web.
     */
    public static void goToLogin(WebDriver driver) {
        clickElementById(driver, "login");
    }

    /**
     * Método estático para ir a la página de solicitud de amistad.
     * @param driver El controlador WebDriver para interactuar con la página web.
     */
    public static void goToFriendRequest(WebDriver driver) {
        //Desplegar el menú
        clickElementById(driver, "navbarDropdown");
        clickElementById(driver, "friendRequest");
    }
}
