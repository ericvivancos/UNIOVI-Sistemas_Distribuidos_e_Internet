package com.example.sdi2324entrega192.pageobjects;


import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Clase para llenar el formulario de registro de usuarios en la vista.
 */
public class PO_SignUpView extends PO_NavView {

    /**
     * Método estático para llenar el formulario de registro de usuarios.
     *
     * @param driver          WebDriver apuntando al navegador actual.
     * @param email           Correo electrónico del usuario.
     * @param name            Nombre del usuario.
     * @param lastName        Apellido del usuario.
     * @param password        Contraseña del usuario.
     * @param passwordConfirm Confirmación de la contraseña del usuario.
     */
    static public void fillForm(WebDriver driver, String email, String name, String lastName, String password, String passwordConfirm) {
        SeleniumUtils.fillFieldBy(driver, "email", email);
        SeleniumUtils.fillFieldBy(driver, "name", name);
        SeleniumUtils.fillFieldBy(driver, "lastName", lastName);
        SeleniumUtils.fillFieldBy(driver, "password", password);
        SeleniumUtils.fillFieldBy(driver, "passwordConfirm", passwordConfirm);

        SeleniumUtils.clickElementBy(driver, By.className("btn"));
    }
    /**
     * Llena el formulario de registro y maneja alertas no controladas.
     * @param driver WebDriver que representa la instancia del navegador.
     * @param email Correo electrónico del usuario.
     * @param name Nombre del usuario.
     * @param lastName Apellido del usuario.
     * @param password Contraseña del usuario.
     * @param passwordConfirmation Confirmación de la contraseña del usuario.
     */
    static public void fillAndCheckForm(WebDriver driver,String email, String name, String lastName, String password, String passwordConfirmation) {
        try {
            PO_SignUpView.fillForm(driver, email, name, lastName, password, passwordConfirmation);
        } catch (UnhandledAlertException alertException) {
            System.out.println("Se ha mostrado una alerta al intentar enviar el formulario con datos inválidos.");
        }
    }
    /**
     * Comprueba si se muestra un mensaje de error cuando se proporciona un formato de correo electrónico incorrecto.
     * @param driver WebDriver que representa la instancia del navegador.
     * @param email Correo electrónico del usuario.
     */
    static public void checkInvalidEmailFormat(WebDriver driver,String email) {
        PO_SignUpView.fillForm(driver, email, "Eric", "Vivancos Yagües", "a123456789A+", "a123456789A+");
        String checkTextEmailFormat = PO_HomeView.getP().getString("Error.signup.email.format", PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver,checkTextEmailFormat);
    }
    /**
     * Comprueba si se muestra un mensaje de error cuando se proporciona una contraseña débil.
     * @param driver WebDriver que representa la instancia del navegador.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     */
    static public void checkWeakPassword(WebDriver driver,String email, String password) {
        PO_SignUpView.fillForm(driver, email, "Eric", "Vivancos Yagües", password, "a123456789A+");
        String checkTextWeakPassword = PO_HomeView.getP().getString("Error.signup.password.weak", PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver,checkTextWeakPassword);
    }
    /**
     * Comprueba si se muestra un mensaje de error cuando la confirmación de la contraseña no coincide.
     * @param driver WebDriver que representa la instancia del navegador.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param passwordConfirm Confirmación de la contraseña del usuario.
     */
    static public void checkPasswordConfirmation(WebDriver driver, String email, String password, String passwordConfirm) {
        PO_SignUpView.fillForm(driver, email, "Eric", "Vivancos Yagües", password, passwordConfirm);
        String checkTextPasswordMismatch = PO_HomeView.getP().getString("Error.signup.passwordConfirm.coincidence", PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver,checkTextPasswordMismatch);
    }
    /**
     * Comprueba si se muestra un mensaje de error cuando se proporciona un correo electrónico que ya está en uso.
     * @param driver WebDriver que representa la instancia del navegador.
     * @param existingEmail Correo electrónico que ya está en uso.
     */
    static public void checkExistingEmail(WebDriver driver, String existingEmail) {
        PO_SignUpView.fillForm(driver, existingEmail, "Eric", "Vivancos Yagües", "a123456789A+", "a123456789A+");
        String checkTextExistingEmail = PO_HomeView.getP().getString("Error.signup.email.duplicate", PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkTextExistingEmail);
        assertEquals(checkTextExistingEmail, result.get(0).getText());
    }
}
