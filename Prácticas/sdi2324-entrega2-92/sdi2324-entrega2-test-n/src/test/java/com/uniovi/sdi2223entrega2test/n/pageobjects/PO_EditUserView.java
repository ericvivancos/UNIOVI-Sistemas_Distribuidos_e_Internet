package com.uniovi.sdi2223entrega2test.n.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class PO_EditUserView extends PO_NavView{

    //metodo quer rellena el formulario de editar usuario
    public static void fillForm(WebDriver driver, String mail, String nombreEditado, String apellidoEditado, int i) {
        WebElement dni = driver.findElement(By.name("email"));
        dni.click();
        dni.clear();
        dni.sendKeys(mail);
        WebElement name = driver.findElement(By.name("name"));
        name.click();
        name.clear();
        name.sendKeys(nombreEditado);

        WebElement surname = driver.findElement(By.name("surname"));
        surname.click();
        surname.clear();
        surname.sendKeys(apellidoEditado);
        //del combox selecionar la ipcion que dice i
        WebElement selectElement = driver.findElement(By.xpath("//select[@id='role']"));
        Select select = new Select(selectElement);
        // Selecciona el segundo elemento del combobox
        select.selectByIndex(1);  // El índice 0 es el primer elemento

        //Pulsar el boton de Alta.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}
