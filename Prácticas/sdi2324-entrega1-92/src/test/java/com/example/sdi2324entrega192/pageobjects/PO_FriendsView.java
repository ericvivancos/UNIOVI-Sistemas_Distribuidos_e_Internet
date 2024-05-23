package com.example.sdi2324entrega192.pageobjects;
import org.openqa.selenium.WebDriver;

/**
 * Clase para navegar a diferentes vistas relacionadas con amigos en la página web.
 */
public class PO_FriendsView extends PO_View {

    /**
     * Método para navegar a la vista de invitaciones de amigos.
     *
     * @param driver El controlador web.
     */
    public static void goInvite(WebDriver driver) {
        driver.get("localhost:8090/friendRequest/list");
    }

    /**
     * Método para navegar a la lista de amigos.
     *
     * @param driver El controlador web.
     */
    public static void goListFriend(WebDriver driver) {
        driver.get("localhost:8090/friends/list");
    }
}
