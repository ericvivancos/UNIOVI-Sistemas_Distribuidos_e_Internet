package com.example.sdi2324entrega192.pageobjects;


import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Clase para interactuar con la vista de registros (logs).
 */
public class PO_LogsView extends PO_NavView {

    /**
     * Método estático que navega a la página de registros.
     * @param driver El controlador WebDriver para interactuar con la página web.
     */
    public static void go(WebDriver driver) {
        driver.get("localhost:8090/logs/list");
    }

    /**
     * Método estático que obtiene la lista de registros filtrada por el tipo especificado.
     * @param driver El controlador WebDriver para interactuar con la página web.
     * @param type El tipo de registro por el que filtrar.
     * @return Una lista de elementos WebElement que representan los registros filtrados.
     */
    public static List<WebElement> getLogsList(WebDriver driver, String type) {
        WebElement types = driver.findElement(By.id("typesCombo"));
        Select optionSelect = new Select(types);
        optionSelect.selectByValue(type);
        driver.findElement(By.id("updateButton")).click();
        return SeleniumUtils.waitLoadElementsByXpath(driver, "//*[@id=\"tableLogs\"]/table/tbody/tr/td[2]", 500);
    }
}
