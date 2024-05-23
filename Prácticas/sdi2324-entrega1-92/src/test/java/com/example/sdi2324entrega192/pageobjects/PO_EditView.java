package com.example.sdi2324entrega192.pageobjects;

import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Clase para llenar el formulario de edición de usuarios en la vista.
 */
public class PO_EditView {

    /**
     * Llena el formulario de edición de usuarios en la vista.
     *
     * @param driver    El WebDriver para interactuar con la página web.
     * @param email     El nuevo correo electrónico del usuario.
     * @param name      El nuevo nombre del usuario.
     * @param lastName  El nuevo apellido del usuario.
     */
    static public void fillForm(WebDriver driver, String email, String name, String lastName) {
        SeleniumUtils.fillFieldBy(driver, "email", email);
        SeleniumUtils.fillFieldBy(driver, "name", name);
        SeleniumUtils.fillFieldBy(driver, "lastName", lastName);

        SeleniumUtils.clickElementBy(driver, By.className("btn"));
    }
}
