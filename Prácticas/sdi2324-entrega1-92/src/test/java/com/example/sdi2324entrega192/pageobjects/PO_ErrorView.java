package com.example.sdi2324entrega192.pageobjects;

import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Clase para interactuar con la vista de error en la página web.
 */
public class PO_ErrorView extends PO_View {

    /**
     * Obtiene los elementos de texto relacionados con el error en la página.
     *
     * @param driver    El WebDriver para interactuar con la página web.
     * @param language  El idioma en el que se desea obtener el texto.
     * @return          Una lista de elementos web que contienen el texto del error.
     */
    public static List<WebElement> getText(WebDriver driver, int language){
        return SeleniumUtils.waitLoadElementsBy(driver,"text",p.getString("Error.page.notFound",language),getTimeout());
    }
}
