package com.example.sdi2324entrega192.pageobjects;


import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Clase para interactuar con la vista de inicio de sesión.
 */
public class PO_LoginView extends PO_NavView {

    /**
     * Método estático que llena el formulario de inicio de sesión con el correo electrónico y la contraseña proporcionados.
     * @param driver El controlador WebDriver para interactuar con la página web.
     * @param email El correo electrónico del usuario para iniciar sesión.
     * @param password La contraseña del usuario para iniciar sesión.
     */
    static public void fillForm(WebDriver driver, String email, String password) {
        SeleniumUtils.fillFieldBy(driver, "username", email);
        SeleniumUtils.fillFieldBy(driver, "password", password);
        SeleniumUtils.clickElementBy(driver, By.className("btn"));
    }

    /**
     * Método estático que navega a la página de inicio de sesión.
     * @param driver El controlador WebDriver para interactuar con la página web.
     */
    public static void go(WebDriver driver) {
        driver.get("localhost:8090/login");
    }

    /**
     * Método estático que obtiene el mensaje de bienvenida en el idioma especificado.
     * @param driver El controlador WebDriver para interactuar con la página web.
     * @param language El idioma en el que se desea obtener el mensaje.
     * @return Una lista de elementos WebElement que contienen el mensaje de bienvenida.
     */
    public static List<WebElement> getWelcomeMessage(WebDriver driver, int language) {
        return SeleniumUtils.waitLoadElementsBy(driver,"text",p.getString("login.Identificate",language),getTimeout());
    }
}
