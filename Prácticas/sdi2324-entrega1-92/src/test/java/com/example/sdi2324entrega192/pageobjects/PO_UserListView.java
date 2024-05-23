package com.example.sdi2324entrega192.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para interactuar con la vista de la lista de usuarios.
 */
public class PO_UserListView extends PO_NavView{

    /**
     * Método estático para rellenar el campo de búsqueda de usuarios.
     *
     * @param driver     WebDriver apuntando al navegador actual.
     * @param searchText Texto a buscar en el campo de búsqueda de usuarios.
     */
    static public void fillForm(WebDriver driver, String searchText) {
        WebElement search = driver.findElement(By.name("searchText"));
        search.click();
        search.clear();
        search.sendKeys(searchText);

        //Pulsar el boton de Alta.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}





