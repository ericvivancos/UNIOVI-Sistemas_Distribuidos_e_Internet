package com.example.sdi2324entrega192.pageobjects;

import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Clase base para las vistas de navegación para interactuar con la barra de navegación.
 */
public class PO_NavView extends PO_View {
    /**
     * Método estático para hacer clic en una de las opciones principales del navbar y verificar la navegación.
     *
     * @param driver      El controlador WebDriver para interactuar con la página web.
     * @param textOption  El texto de la opción principal en el navbar.
     * @param criterio    El criterio de búsqueda para el elemento de destino ("id", "class", "text", "@attribute" o "free").
     * @param targetText  El texto correspondiente a la búsqueda del elemento en la página destino.
     */
    public static void clickOption(WebDriver driver, String textOption, String criterio, String targetText) {
        //CLickamos en la opción del navbar y esperamos a que se cargue el enlace.
        List<WebElement> elements = SeleniumUtils.waitLoadElementsBy(driver, "@href", textOption,
                getTimeout());
        //Tiene que haber un solo elemento.
        Assertions.assertEquals(1, elements.size());
        //Ahora lo clickamos
        elements.get(0).click();
        //Esperamos a que sea visible un elemento concreto
        elements = SeleniumUtils.waitLoadElementsBy(driver, criterio, targetText, getTimeout());
        //Tiene que haber un solo elemento.
        Assertions.assertEquals(1, elements.size());
    }

    /**
     * Método estático para cambiar el idioma de la página.
     *
     * @param driver       El controlador WebDriver para interactuar con la página web.
     * @param textLanguage El texto que aparece en el enlace de idioma ("English" o "Spanish").
     */
    public static void changeLanguage(WebDriver driver, String textLanguage) {
        //Clickamos la opción Idioma.
        List<WebElement> languageButton = SeleniumUtils.waitLoadElementsBy(driver, "id", "btnLanguage",
                getTimeout());
        languageButton.get(0).click();
        //Esperamos a que aparezca el menú de opciones.
        SeleniumUtils.waitLoadElementsBy(driver, "id", "languageDropdownMenuButton", getTimeout());
        //CLickamos la opción de idioma especificada.
        List<WebElement> selectedLanguage = SeleniumUtils.waitLoadElementsBy(driver, "id", textLanguage,
                getTimeout());
        selectedLanguage.get(0).click();
    }

    /**
     * Método estático para hacer clic en una opción del navbar identificada por su ID y verificar la navegación.
     *
     * @param driver      El controlador WebDriver para interactuar con la página web.
     * @param textOption  El texto de la opción principal en el navbar.
     * @param criterio    El criterio de búsqueda para el elemento de destino ("id", "class", "text", "@attribute" o "free").
     * @param targetText  El texto correspondiente a la búsqueda del elemento en la página destino.
     */
    public static void clickOptionById(WebDriver driver, String textOption, String criterio, String targetText) {
        //CLickamos en la opción del navbar y esperamos a que se cargue el enlace.
        List<WebElement> elements = SeleniumUtils.waitLoadElementsBy(driver, "id", textOption,
                getTimeout());
        //Tiene que haber un solo elemento.
        Assertions.assertEquals(1, elements.size());
        //Ahora lo clickamos
        elements.get(0).click();
        //Esperamos a que sea visible un elemento concreto
        elements = SeleniumUtils.waitLoadElementsBy(driver, criterio, targetText, getTimeout());
        //Tiene que haber un solo elemento.
        Assertions.assertEquals(1, elements.size());
    }
}