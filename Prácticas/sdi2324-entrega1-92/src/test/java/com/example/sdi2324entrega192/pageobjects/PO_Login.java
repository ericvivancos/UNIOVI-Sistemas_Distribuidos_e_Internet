package com.example.sdi2324entrega192.pageobjects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
/**
 * Clase para llenar el formulario de inicio de sesión en la vista.
 */
public class PO_Login  {

    /**
     * Método estático que llena el formulario de inicio de sesión con las credenciales proporcionadas.
     * @param driver El controlador WebDriver para interactuar con la página web.
     * @param emailp El correo electrónico del usuario para iniciar sesión.
     * @param passwordp La contraseña del usuario para iniciar sesión.
     */
    public static void fillForm(WebDriver driver, String emailp, String passwordp){
        WebElement emailField = driver.findElement(By.name("username"));
        emailField.click();
        emailField.clear();
        emailField.sendKeys(emailp);
        WebElement passwordField = driver.findElement(By.name("password"));
        passwordField.click();
        passwordField.clear();
        passwordField.sendKeys(passwordp);
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}