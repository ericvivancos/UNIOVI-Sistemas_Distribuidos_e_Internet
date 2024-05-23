package com.example.sdi2324entrega192.pageobjects;

import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Clase para manejar la página de error de recomendación en la vista.
 */
public class PO_ErrorRecommendation extends PO_View{

    /**
     * Obtiene los elementos de texto relacionados con el error de recomendación en la página.
     *
     * @param driver    El WebDriver para interactuar con la página web.
     * @param language  El idioma en el que se desea obtener el texto.
     * @return          Una lista de elementos web que contienen el texto del error de recomendación.
     */
    public static List<WebElement> getText(WebDriver driver, int language){
        return SeleniumUtils.waitLoadElementsBy(driver,"text",p.getString("Error.page.recommendation",language),getTimeout());
    }
}
