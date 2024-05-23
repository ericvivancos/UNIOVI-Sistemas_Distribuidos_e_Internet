package com.uniovi.sdi2324916spring.pageobjects;

import com.uniovi.sdi2324916spring.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class PO_PrivateView extends PO_NavView {
    /*static public void fillFormAddMark(WebDriver driver, int userOrder, String descriptionp, String scorep)
    {
        //Esperamos 5 segundo a que carge el DOM porque en algunos equipos falla
        SeleniumUtils.waitSeconds(driver, 5);
        //Seleccionamos el alumnos userOrder
        new Select(driver.findElement(By.id("user"))).selectByIndex(userOrder);
        //Rellenemos el campo de descripción
        WebElement description = driver.findElement(By.name("description"));
        description.clear();
        description.sendKeys(descriptionp);
        WebElement score = driver.findElement(By.name("score"));
        score.click();
        score.clear();
        score.sendKeys(scorep);
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }*/


        public static void fillFormAddMark(WebDriver driver, int userOrder, String descriptionp, String scorep) {
            // Seleccionamos el alumno por su orden
            WebElement userSelect = driver.findElement(By.id("user"));
            userSelect.sendKeys(Integer.toString(userOrder));

            // Rellenamos la descripción
            WebElement description = driver.findElement(By.name("description"));
            description.clear();
            description.sendKeys(descriptionp);

            // Rellenamos la puntuación
            WebElement score = driver.findElement(By.name("score"));
            score.clear();
            score.sendKeys(scorep);

            // Hacemos clic en el botón de envío
            WebElement boton = driver.findElement(By.className("btn"));
            boton.click();
        }

        public static boolean isAlertPresent(WebDriver driver) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, 2);
                wait.until(ExpectedConditions.alertIsPresent());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        public static boolean isInPrivateView(WebDriver driver) {
            String checkText = "Notas del usuario";
            List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
            return !result.isEmpty();
        }

        public static List<WebElement> getMarkElements(WebDriver driver) {
            return SeleniumUtils.waitLoadElementsBy(driver, "free", "//tbody/tr", PO_View.getTimeout());
        }

        public static void logout(WebDriver driver) {
            // Seleccionar la opción de logout
            String loginText = PO_HomeView.getP().getString("signup.message", PO_Properties.getSPANISH());
            PO_PrivateView.clickOption(driver, "logout", "text", loginText);
        }


}
