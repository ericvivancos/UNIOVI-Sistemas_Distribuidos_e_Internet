package com.example.sdi2324entrega192.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Clase para llenar el formulario de publicación en la vista.
 */
public class PO_PostView {
    /**
     * Método estático para llenar el formulario de publicación en la vista.
     *
     * @param driver    El controlador WebDriver para interactuar con la página web.
     * @param titlep    El título de la publicación.
     * @param contentp  El contenido de la publicación.
     */
    public static void fillForm(WebDriver driver, String titlep, String contentp){
        WebElement titleField = driver.findElement(By.name("title"));
        titleField.click();
        titleField.clear();
        titleField.sendKeys(titlep);
        WebElement contentField = driver.findElement(By.name("content"));
        contentField.click();
        contentField.clear();
        contentField.sendKeys(contentp);
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}