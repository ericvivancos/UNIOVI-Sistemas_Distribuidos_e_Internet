package com.example.sdi2324entrega192;


import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.pageobjects.*;
import com.example.sdi2324entrega192.repositories.UsersRepository;
import com.example.sdi2324entrega192.util.SeleniumUtils;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;


import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Nested
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class Sdi2324Entrega192ApplicationTests {

    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String Geckodriver = "geckodriver-v0.30.0-win64.exe";
    //Común a Windows y a MACOSX
    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    static String URL = "http://localhost:8090";

    @Autowired
    private UsersRepository usersRepository;

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    @BeforeEach
    public void setUp() {
        driver.navigate().to(URL);
        driver.manage().deleteAllCookies();
    }

    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    //Antes de la primera prueba
    @BeforeAll
    static public void begin() {
    }

    //Al finalizar la última prueba
    @AfterAll
    static public void end() {
        //Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }



    @Test
    @Transactional

    @Order(1)
        // Prueba1: Registro de Usuario con datos válidos(Julio) V
    void Prueba1() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "uo303985@gmail.com", "Julio", "Vivancos Yagües", "a123456789A+", "a123456789A+");
        String checkText = PO_View.getP().getString("welcome.message",PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        assertEquals(checkText, result.get(0).getText());
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        // Nos logueamos como admin
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        WebElement lastPageButton = driver.findElement(By.className("last-page"));
        //WebElement lastPageButton = driver.findElement(By.xpath("//a[text()='#{' + @{page.last} + '}']"));
        lastPageButton.click();
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        SeleniumUtils.clickCheckboxInRow(filas.get(filas.size()-1));
        SeleniumUtils.clickElementBy(driver,By.id("deleteButton"));

    }
    // Prueba2: Registro de Usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos y contraseña incorrecta (débil)).
    @Test
    @Transactional

    @Order(2)
    void Prueba2() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        // Llenar el formulario de registro con diferentes escenarios y manejar la alerta si se presenta
        PO_SignUpView.fillAndCheckForm(driver, "", "Eric", "Vivancos Yagües", "a123456789A+", "a123456789A+");
        PO_SignUpView.fillAndCheckForm(driver, "uo303984@gmail.com", "", "Vivancos Yagües", "a123456789A+", "a123456789A+");
        PO_SignUpView.fillAndCheckForm(driver, "uo303984@gmail.com", "Eric", "", "a123456789A+", "a123456789A+");
        PO_SignUpView.checkInvalidEmailFormat(driver, "uo303984");
        // Sin minuscula
        PO_SignUpView.checkWeakPassword(driver, "uo303984@gmail.com", "0123456789A+");
        // Sin mayuscula
        PO_SignUpView.checkWeakPassword(driver, "uo303984@gmail.com", "a0123456789+");
        // Sin carácter especial
        PO_SignUpView.checkWeakPassword(driver, "uo303984@gmail.com", "a0123456789A");
        // Sin números
        PO_SignUpView.checkWeakPassword(driver, "uo303984@gmail.com", "aaaaaaaaaaA+");
        // Sin ser 12 carácteres
        PO_SignUpView.checkWeakPassword(driver, "uo303984@gmail.com", "a12345678A+");
    }
    // Prueba3: Registro de Usuario con datos inválidos (repetición de contraseña inválida).
    @Test
    @Transactional

    @Order(3)
    void Prueba3() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.checkPasswordConfirmation(driver, "uo303984@gmail.com", "a123456789A+", "a123456789A");

    }
    // Prueba4: Registro de Usuario con datos inválidos (email existente).
    @Test
    @Transactional

    @Order(4)
    void Prueba4() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.checkExistingEmail(driver, "admin@email.com");
    }

    // Prueba5: Inicio de sesión con datos válidos (administrador).
    @Test
    @Transactional

    @Order(5)
    void Prueba5() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        //intenta un administrador logearse
        PO_Login.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        //comprobar que estas en la pagina privada
        String checkText = PO_View.getP().getString("home.msg.private",PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        checkText = "Administrador";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }

    // Prueba6: Inicio de sesión con datos válidos (usuario estándar).
    @Test
    @Transactional

    @Order(6)
    void Prueba6() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");

        //intenta un administrador logearse
        PO_Login.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");
        //comprobar que estas en la pagina privada
        String checkText = PO_View.getP().getString("home.msg.private",PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        checkText = "Usuario";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

    }

    // Prueba7: Inicio de sesión con datos inválidos (usuario estándar, campos email y contraseña vacíos).
    @Test
    @Transactional

    @Order(7)
    void Prueba7() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");

        //intenta un administrador logearse
        PO_Login.fillForm(driver, "", "");
        //comprobar que estas en la pagina del login
        String checkText =  PO_View.getP().getString("login.message",PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //comprobar que pide que los campos son obligatorios

        // comprobar que se muestra un mensaje indicando que los campos son obligatorios
        checkText = PO_View.getP().getString("login.credencialesInvalidas",PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

    }

    // Prueba8: Inicio de sesión con datos válidos (usuario estándar, email existente, pero contraseña incorrecta).

    @Test
    @Transactional

    @Order(8)
    void Prueba8() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");

        //intenta un administrador logearse
        PO_Login.fillForm(driver, "admin@email.com", "contraIncorrecta");

        //comprobar que estas en la pagina del login
        String checkText = PO_View.getP().getString("login.Identificate",PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //comprobar que salio el error en la pagina del login
        checkText = PO_View.getP().getString("login.credencialesInvalidas",PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver, checkText);


    }
    // Prueba9: Hacer clic en la opción de cerrar sesión y comprobar que se muestra el mensaje "Ha cerrado sesión correctamente" y se redirige a la página de inicio de sesión.
    @Order(9)
    @Test
    @Transactional

    void Prueba9() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        String checkText = PO_View.getP().getString("logout.message",PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        assertEquals(checkText, result.get(0).getText());
    }
    // Prueba10: Comprobar que el botón de cerrar sesión no está visible si el usuario no está autenticado.
    @Order(10)
    @Test
    @Transactional

    void Prueba10() {
        try{
            driver.findElement(By.id("logout"));
            assertTrue(false);
        }catch(NoSuchElementException e){
            //El test pasa si no encuentra el botón sin logearse
            assertTrue(true);
        }
    }
    // Prueba11: Mostrar el listado de usuarios y comprobar que se muestran todos los usuarios existentes en el sistema, incluyendo el usuario actual y los usuarios administradores.
    @Order(11)
    @Test
    @Transactional

    void Prueba11() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "admin@email.com");
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(5, filas.size());
        driver.findElement(By.className("next")).click();
        List<WebElement> filas2 = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(5, filas2.size());
        driver.findElement(By.className("next")).click();
        List<WebElement> filas3 = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(5, filas3.size());
        driver.findElement(By.className("next")).click();
        List<WebElement> filas4 = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(3, filas4.size());
    }
    // Prueba12: Autenticarse como administrador, editar un usuario estándar cambiando su rol a administrador, email, nombre y apellidos. Comprobar que los datos se han actualizado correctamente. Salir de sesión como administrador y autenticarse como el usuario modificado para acceder a la funcionalidad de listado de usuarios del sistema y probar el nuevo rol de administrador.
    @Order(12)
    @Test

    void Prueba12() {
        //Inicio sesión como admin
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Email");
        SeleniumUtils.clickElementBy(driver, By.className("edit"));
        SeleniumUtils.clickElementBy(driver, By.className("role"));
        //Edito el rol para que sea admin
        SeleniumUtils.clickElementBy(driver, By.className("ROLE_ADMIN"));
        PO_EditView.fillForm(driver, "a@email.com", "Sara", "Inés");
        String checkTextEdit = "Sara";
        SeleniumUtils.textIsPresentOnPage(driver,checkTextEdit);
        //Cierro sesión
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        //Inicio sesión con el usuario editado
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "a@email.com", "123456");
        //Compruebo que funciona como admin
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "admin@email.com");
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
    }

    // Prueba13: Editar un usuario introduciendo datos inválidos (email existente asignado a otro usuario del sistema, nombre y apellidos vacíos). Comprobar que se devuelven los mensajes de error correctamente y que el usuario no se actualiza.
    @Order(13)
    @Test
    @Transactional

    void Prueba13() {
        //
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Email");
        SeleniumUtils.clickElementBy(driver, By.className("edit"));
        PO_EditView.fillForm(driver, "user15@email.com", "", "");

        String checkTextEmailFormat = PO_HomeView.getP().getString("Error.signup.email.duplicate", PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver,checkTextEmailFormat);

        String checkTextNameFormat = PO_HomeView.getP().getString("Error.empty", PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver,checkTextNameFormat);

    }
    // Prueba14: Ir a la lista de usuarios, borrar el primer usuario de la lista y comprobar que la lista se actualiza y dicho usuario desaparece.
    @Order(14)
    @Test
    @Transactional

    void Prueba14(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        SeleniumUtils.clickElementBy(driver,By.id("deleteButton"));
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("Error.noSelectedUser",PO_Properties.getSPANISH()));
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        SeleniumUtils.clickCheckboxInRow(filas.get(0));
        SeleniumUtils.clickElementBy(driver,By.id("deleteButton"));
        PO_UserListView.fillForm(driver, "uo303984@uniovi.es");
        filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(0, filas.size());
    }
    // Prueba15: Ir a la lista de usuarios, borrar el último usuario de la lista y comprobar que la lista se actualiza y dicho usuario desaparece.
    @Order(15)
    @Test
    @Transactional

    void Prueba15(){
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "uo303985@gmail.com", "Julio", "Vivancos Yagües", "a123456789A+", "a123456789A+");
        String checkText = PO_View.getP().getString("welcome.message",PO_Properties.getSPANISH());
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        assertEquals(checkText, result.get(0).getText());
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        WebElement lastPageButton = driver.findElement(By.xpath("//a[text()='Última']"));
        lastPageButton.click();
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        SeleniumUtils.clickCheckboxInRow(filas.get(filas.size()-1));
        SeleniumUtils.clickElementBy(driver,By.id("deleteButton"));
        PO_UserListView.fillForm(driver, "Julio");
        filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(0, filas.size());
    }
    // Prueba16: Ir a la lista de usuarios, borrar 3 usuarios y comprobar que la lista se actualiza y dichos usuarios desaparecen.
    @Order(16)
    @Test
    @Transactional

    // INTENTAMOS BORRARNOS A NOSOTROS CON FRACASO Y BORRAMOS A DOS USUARIOS QUE PREVIAMENTE VAMOS A CREAR
    void Prueba16(){
        // Creo a Antonio
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "Antonio@gmail.com", "Antonio", "Martínez", "a123456789A+", "a123456789A+");
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        // Creo a Samuel
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "Samuel@gmail.com", "Samuel", "Martínez", "a123456789A+", "a123456789A+");
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        // Nos logueamos como admin
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        WebElement lastPageButton = driver.findElement(By.className("last-page"));
        lastPageButton.click();
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        SeleniumUtils.clickCheckboxInRow(filas.get(filas.size()-1));
        SeleniumUtils.clickCheckboxInRow(filas.get(filas.size()-2));
        SeleniumUtils.clickElementBy(driver,By.id("deleteButton"));
        PO_UserListView.fillForm(driver, "Antonio@gmail.com");
        filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(0, filas.size());
        PO_UserListView.fillForm(driver, "Samuel@gmail.com");
        filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(0, filas.size());
    }
    // Prueba17: Mostrar el listado de usuarios y comprobar que se muestran todos los usuarios existentes en el sistema, excepto el propio usuario y aquellos que sean administradores.
    @Test
    @Order(17)
    @Transactional

    void Prueba17() {
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "user02@email.com");
        SeleniumUtils.textIsNotPresentOnPage(driver, "user01@email.com");
        SeleniumUtils.textIsNotPresentOnPage(driver, "admin@email.com");
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        //comprobando la paginación
        assertEquals(5, filas.size());
    }
    // Prueba18: Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que corresponde con el listado de usuarios existentes en el sistema.
    @Order(18)
    @Test
    @Transactional

    void Prueba18() {
        //Iniciamos sesión con el usuario administrador
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        //Clicamos en la opción de ver usuarios
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        //Buscamos una cadena vacía
        PO_UserListView.fillForm(driver, "");
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(5, filas.size());
    }
    // Prueba19: Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se muestra la página que corresponde, con la lista de usuarios vacía.
    @Order(19)
    @Test
    @Transactional

    void Prueba19() {
        //Iniciamos sesión con el usuario administrador
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        //Clicamos en la opción de ver usuarios
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        //Buscamos una cadena que no se encuentre entre el nombre, apellido o email de los usuarios
        PO_UserListView.fillForm(driver, "Hola");
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(0, filas.size());
    }
    // Prueba20: Hacer una búsqueda con un texto específico y comprobar que se muestra la página que corresponde, con la lista de usuarios en los que el texto especificado sea parte de su nombre, apellidos o email.
    @Order(20)
    @Test
    @Transactional

    void Prueba20() {
        //Iniciamos sesión con el usuario administrador
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        //Clicamos en la opción de ver usuarios
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        //Buscamos por un nombre de usuario específico
        PO_UserListView.fillForm(driver, "Pedro");
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(1, filas.size());

        //Buscamos por un apellido específico
        PO_UserListView.fillForm(driver, "Rodriguez");
        filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(1, filas.size());

        //Buscamos un correo específico
        PO_UserListView.fillForm(driver, "user06@email.com");
        filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(1, filas.size());
    }
    // Prueba21: Desde el listado de usuarios de la aplicación, enviar una invitación de amistad a un usuario y comprobar que la solicitud de amistad aparece en el listado de invitaciones.
    @Order(21)
    @Test

    @Transactional
    void Prueba21(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        // Encontrar la primera fila y hacer clic en el botón "Agregar amigo"
        SeleniumUtils.clickElementBy(driver,By.className("last-page"));
        // Encontrar la primera fila y hacer clic en el botón "Agregar amigo"
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr")); // Cambia [1] por el número de la fila que deseas
        WebElement filaUsuario = filas.get(filas.size()-1);
        // Encuentra el formulario dentro de esa fila
        WebElement formularioAgregarAmigo = filaUsuario.findElement(By.tagName("form"));
        // Encuentra el botón "Agregar amigo" dentro del formulario
        WebElement botonAgregarAmigo = formularioAgregarAmigo.findElement(By.tagName("button"));
        botonAgregarAmigo.click();

        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user16@email.com", "Us3r@16-PASSW");
        // Encuentra el botón "Gestión de Amitades" por su ID
        WebElement botonGestionAmistades = driver.findElement(By.id("navbarDropdown"));

        // Haz clic en el botón "Gestión de Amitades"
        botonGestionAmistades.click();
        // Encuentra la opción "Ver solicitudes" dentro del menú desplegable por su texto
        WebElement opcionVerSolicitudes = driver.findElement(By.xpath("//a[@id='friendRequest']"));

        // Haz clic en la opción "Ver solicitudes"
        opcionVerSolicitudes.click();

        List<WebElement> filas2 = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(1, filas2.size());

    }
    // Prueba22: Desde el listado de usuarios de la aplicación, intentar enviar una invitación de amistad a un usuario al que ya se le había enviado la invitación previamente.
    @Order(22)
    @Test
    @Transactional

    void Prueba22(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user15@email.com", "Us3r@15-PASSW");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");

        SeleniumUtils.clickElementBy(driver,By.className("last-page"));
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        WebElement filaUsuario = filas.get(filas.size()-1);
        WebElement formularioAgregarAmigo = filaUsuario.findElement(By.tagName("form"));
        // Encuentra el botón "Agregar amigo" dentro del formulario
        WebElement botonAgregarAmigo = formularioAgregarAmigo.findElement(By.tagName("button"));
        botonAgregarAmigo.click();
        SeleniumUtils.clickElementBy(driver,By.className("last-page"));
        filas = driver.findElements(By.xpath("//tbody/tr"));
        filaUsuario = filas.get(filas.size()-1);
        formularioAgregarAmigo = filaUsuario.findElement(By.tagName("form"));
        WebElement botonAgregarAmigo2 = formularioAgregarAmigo.findElement(By.tagName("button"));
        botonAgregarAmigo2.click();
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("Error.existsfriendRequest",PO_Properties.getSPANISH()));

        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user16@email.com", "Us3r@16-PASSW");
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        // Encontrar la primera fila y hacer clic en el botón "Agregar amigo"
        SeleniumUtils.clickElementBy(driver,By.className("last-page"));
        filas = driver.findElements(By.xpath("//tbody/tr"));
        filaUsuario = filas.get(filas.size()-1);
        formularioAgregarAmigo = filaUsuario.findElement(By.tagName("form"));
        formularioAgregarAmigo.findElement(By.tagName("button")).click();
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("Error.existsfriendRequest",PO_Properties.getSPANISH()));

    }
    // Prueba23: Mostrar el listado de invitaciones de amistad recibidas y comprobar con un listado que contenga varias invitaciones recibidas.
    @Order(23)
    @Test
    @Transactional

    void Prueba23(){
        //agregar otra solicitud de amistad extra
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user11@email.com", "Us3r@11-PASSW");
        //ir a ver solicitudes de amistad
        PO_HomeView.clickOptionById(driver, "navbarDropdown", "id", "friendRequest");
        //comprobamos que podemos ver la tabla de solicitudes
        PO_HomeView.clickOptionById(driver, "friendRequest", "text", "Nombre del solicitante");
        //la primera pagina esta completa de invitaciones se espera 5
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='friendRequestTable']//tbody/tr"));
        // Asegurarse de que haya exactamente una fila
        Assertions.assertEquals(5, rows.size(), "La tabla debe tener una sola fila");

        //asegurarse de que todas las columnas de la tabla no esten vacias y tetnga la fecha

        // Iterar sobre las filas y verificar las fechas
        for (int i = 1; i <= 5; i++) {
            String fechaXpath = String.format("//*[@id='friendRequestTable']//tbody/tr[%d]/td[3]", i);
            WebElement fechaElement = driver.findElement(By.xpath(fechaXpath));

            // Asegurarse de que la celda de fecha no esté vacía
            Assertions.assertFalse(fechaElement.getText().isEmpty(), "La celda de fecha no debe estar vacía");
        }


    }
    // Prueba24: Sobre el listado de invitaciones recibidas, hacer clic en el botón o enlace de una de ellas y comprobar que dicha solicitud desaparece del listado de invitaciones.
    @Order(24)
    @Test
    @Transactional

    void Prueba24(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user04@email.com", "Us3r@4-PASSW");
        //Primero vamos a realizar una petición de amistad
        PO_HomeView.clickOptionById(driver, "userDropdown", "id", "verUsuariosOption");
        PO_HomeView.clickOptionById(driver, "verUsuariosOption", "text", "Listado de usuarios");
        // Encontrar la primera fila y hacer clic en el botón "Agregar amigo"
        SeleniumUtils.clickElementBy(driver,By.className("last-page"));
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr")); // Cambia [1] por el número de la fila que deseas
        WebElement filaUsuario = filas.get(0);//user 16
        // Encuentra el formulario dentro de esa fila
        WebElement formularioAgregarAmigo = filaUsuario.findElement(By.tagName("form"));
        // Encuentra el botón "Agregar amigo" dentro del formulario
        WebElement botonAgregarAmigo = formularioAgregarAmigo.findElement(By.tagName("button"));
        botonAgregarAmigo.click();

        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user16@email.com", "Us3r@16-PASSW");

        //Accedemos a las solicitudes de amistad
        PO_HomeView.clickOptionById(driver, "navbarDropdown", "id", "friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text", "Nombre del solicitante");

        //Pulsamos el botón de acceptar
        List<WebElement> filasR = driver.findElements(By.xpath("//tbody/tr")); // Cambia [1] por el número de la fila que deseas
        WebElement filaUsuarioR = filasR.get(filasR.size()-1);
        WebElement botonAcceptarSolicitud = filaUsuarioR.findElement(By.xpath("//td[4]/a"));
        botonAcceptarSolicitud.click();

        //Nos aseguramos que la solicitud no salga, como solo era una, comprobamos que no hay solicitudes
        PO_HomeView.clickOptionById(driver, "navbarDropdown", "id", "friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text", "Nombre del solicitante");

        //SeleniumUtils.textIsNotPresentOnPage(driver,"user04@email.com");
    }
    // Prueba25: Mostrar el listado de amigos de un usuario y comprobar que el listado contiene los amigos que deben ser.
    @Order(25)
    @Test
    @Transactional

    void Prueba25(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user02@email.com", "Us3r@2-PASSW");
        //ir a ver amigos

        PO_HomeView.clickOptionById(driver, "navbarDropdown", "id", "friendsList");
        PO_HomeView.clickOptionById(driver, "friendsList", "text", "Lista de amigos");

        Date date = Date.valueOf(LocalDate.now());



        SeleniumUtils.textIsPresentOnPage(driver,"user08@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"sergio");
        SeleniumUtils.textIsPresentOnPage(driver,"Sanchez");
        SeleniumUtils.textIsPresentOnPage(driver,date.toString());
        // Encontrar todas las filas de la tabla
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='friendsTable']//tbody/tr"));

        // Asegurarse de que haya exactamente una fila
        Assertions.assertEquals(2, rows.size(), "La tabla debe tener una sola fila");



    }
    // Prueba26: Mostrar el listado de amigos de un usuario y comprobar que se incluye la información relacionada con la última publicación de cada usuario y la fecha de inicio de amistad.
    @Order(26)
    @Test
    @Transactional

    void Prueba26(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");
        //ir a ver amigos

        PO_HomeView.clickOptionById(driver, "navbarDropdown", "id", "friendsList");
        PO_HomeView.clickOptionById(driver, "friendsList", "text", "Lista de amigos");


        SeleniumUtils.textIsPresentOnPage(driver,"user02@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user03@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user04@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user05@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user06@email.com");


        Date date = Date.valueOf(LocalDate.now());

        //comprobar que las fechas aparecen bien
        SeleniumUtils.textIsPresentOnPage(driver,date.toString());
        SeleniumUtils.textIsPresentOnPage(driver,date.toString());
//        SeleniumUtils.textIsPresentOnPage(driver,"Este usuario aún no ha realizado ninguna publicación");

        // Encontrar todas las filas de la tabla
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='friendsTable']//tbody/tr"));

        // Iterar sobre cada fila y verificar que los campos de fecha y última publicación no estén vacíos
        for (int i = 1; i <= rows.size(); i++) {
            // XPath para la fecha en la i-ésima fila
            String fechaXPath = String.format("/html/body/div/div/table/tbody/tr[%d]/td[4]", i);
            // XPath para la última publicación en la i-ésima fila
            String ultimaPublicacionXPath = String.format("/html/body/div/div/table/tbody/tr[%d]/td[5]", i);

            // Obtener el texto de los campos de fecha y última publicación
            String fecha = driver.findElement(By.xpath(fechaXPath)).getText();
            String ultimaPublicacion = driver.findElement(By.xpath(ultimaPublicacionXPath)).getText();

            Assertions.assertFalse(fecha.trim().isEmpty(), "El campo de fecha no debe estar vacío en la fila " + i);
            Assertions.assertFalse(ultimaPublicacion.trim().isEmpty(), "El campo de última publicación no debe estar vacío en la fila " + i);
        }

        // Asegurarse de que la pagina este completa
        Assertions.assertEquals(5, rows.size(), "La tabla debe tener una sola fila");




    }
    // Prueba27: Ir al formulario para crear publicaciones, rellenarlo con datos válidos y pulsar el botón Submit. Comprobar que la publicación aparece en el listado de publicaciones del usuario.
    @Order(27)
    @Test
    @Transactional

    void Prueba27(){
        // Navega a la página de inicio y haz clic en el botón de login
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        // Completa el formulario de inicio de sesión con credenciales válidas
        PO_LoginView.fillForm(driver, "user16@email.com", "Us3r@16-PASSW");

        // Espera a que se cargue la página de inicio después de iniciar sesión
        //PO_View.checkElement(driver, "id", "navbarDropdown");
        //SeleniumUtils.waitLoadElementsBy(driver,"id","navbarDropdown",0);

        // Haz clic en la opción "Crear publicación" en el menú de navegación
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "postAdd");
        PO_HomeView.clickOptionById(driver, "postAdd", "text", PO_View.getP().getString("posts.publish",PO_Properties.getSPANISH()));

        // Espera a que se cargue la página de creación de publicación
        SeleniumUtils.waitLoadElementsBy(driver, "id", "title", 1);

        // Rellena el formulario de creación de publicación
        WebElement titleInput = driver.findElement(By.id("title"));
        titleInput.sendKeys("Esto es una prueba");

        WebElement contentInput = driver.findElement(By.id("content"));
        contentInput.sendKeys("Contenido de la publicación");

        // Haz clic en el botón "Publicar"
        WebElement publishButton = driver.findElement(By.xpath("//button[@type='submit']"));
        publishButton.click();

        // Haz clic en la opción "Crear publicación" en el menú de navegación
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "ownedPosts");
        PO_HomeView.clickOptionById(driver, "ownedPosts", "text", PO_View.getP().getString("posts.title.head",PO_Properties.getSPANISH()));

        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(1, filas.size());
        SeleniumUtils.textIsPresentOnPage(driver,"Esto es una prueba");
    }
    // Prueba28: Ir al formulario de crear publicaciones, rellenarlo con datos inválidos (campos título y descripción vacíos) y pulsar el botón Submit. Comprobar que se muestran los mensajes de campo obligatorios
    @Order(28)
    @Test
    @Transactional

    void Prueba28(){
        // Navega a la página de inicio y haz clic en el botón de login
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        // Completa el formulario de inicio de sesión con credenciales válidas
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");

        // Espera a que se cargue la página de inicio después de iniciar sesión
        //PO_View.checkElement(driver, "id", "navbarDropdown");
//        SeleniumUtils.waitLoadElementsBy(driver,"id","navbarDropdown",0);

        // Haz clic en la opción "Crear publicación" en el menú de navegación
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "postAdd");
        PO_HomeView.clickOptionById(driver, "postAdd", "text", PO_View.getP().getString("posts.publish",PO_Properties.getSPANISH()));

        // Espera a que se cargue la página de creación de publicación
        SeleniumUtils.waitLoadElementsBy(driver, "id", "title", 1);





        // Haz clic en el botón "Publicar"
        WebElement publishButton = driver.findElement(By.xpath("//button[@type='submit']"));
        publishButton.click();

        String checkTextNameFormat = PO_HomeView.getP().getString("Error.empty", PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver,checkTextNameFormat);

        // Rellena el formulario de creación de publicación
        WebElement titleInput = driver.findElement(By.id("title"));
        titleInput.sendKeys("");

        WebElement contentInput = driver.findElement(By.id("content"));
        contentInput.sendKeys("HOLA");

        SeleniumUtils.textIsPresentOnPage(driver,checkTextNameFormat);

        titleInput.sendKeys("hOLA");
        contentInput.sendKeys("");

        SeleniumUtils.textIsPresentOnPage(driver,checkTextNameFormat);
    }
    // Prueba29: Mostrar el listado de publicaciones de un usuario y comprobar que se muestran todas las que existen para dicho usuario.
    @Test
    @Order(29)
    @Transactional

    void Prueba29() {
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "uo303985@gmail.com", "Julio", "Vivancos Yagües", "a123456789A+", "a123456789A+");
        // Navega a la página de inicio y haz clic en el botón de login
        //PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        // Completa el formulario de inicio de sesión con credenciales válidas
        //PO_LoginView.fillForm(driver, "user02@email.com", "Us3r@2-PASSW");

        // Espera a que se cargue la página de inicio después de iniciar sesión
        //PO_View.checkElement(driver, "id", "navbarDropdown");
        SeleniumUtils.waitLoadElementsBy(driver,"id","navbarDropdown",0);

        // Haz clic en la opción "Crear publicación" en el menú de navegación
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "postAdd");
        PO_HomeView.clickOptionById(driver, "postAdd", "text", PO_View.getP().getString("posts.publish",PO_Properties.getSPANISH()));

        // Espera a que se cargue la página de creación de publicación
        SeleniumUtils.waitLoadElementsBy(driver, "id", "title", 1);

        // Rellena el formulario de creación de publicación
        WebElement titleInput = driver.findElement(By.id("title"));
        titleInput.sendKeys("Esto es una prueba");

        WebElement contentInput = driver.findElement(By.id("content"));
        contentInput.sendKeys("Contenido de la publicación");

        // Haz clic en el botón "Publicar"
        WebElement publishButton = driver.findElement(By.xpath("//button[@type='submit']"));
        publishButton.click();

        // Ver publicación
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "ownedPosts");
        PO_HomeView.clickOptionById(driver, "ownedPosts", "text", PO_View.getP().getString("posts.title.head",PO_Properties.getSPANISH()));

        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(1, filas.size());
        SeleniumUtils.textIsPresentOnPage(driver,"Esto es una prueba");

        //Comprobación de que se listan con el título, la fecha y el texto
        SeleniumUtils.textIsPresentOnPage(driver,"Fecha");
        SeleniumUtils.textIsPresentOnPage(driver, "Título");
        SeleniumUtils.textIsPresentOnPage(driver,"Contenido");


    }
    // Prueba30: Mostrar el perfil del usuario y comprobar que se muestran sus datos y el listado de sus publicaciones.
    @Order(30)
    @Test
    @Transactional

    void Prueba30(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");
        //ir a ver amigos

        PO_HomeView.clickOptionById(driver, "navbarDropdown", "id", "friendsList");
        PO_HomeView.clickOptionById(driver, "friendsList", "text", "Lista de amigos");


        SeleniumUtils.textIsPresentOnPage(driver,"user02@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user03@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user04@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user05@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user06@email.com");

        //Se accede a la página de Juan (es el primer usuario amigo)
        SeleniumUtils.clickElementBy(driver, By.className("name"));


        SeleniumUtils.textIsPresentOnPage(driver,"Fecha");
        SeleniumUtils.textIsPresentOnPage(driver, "Título");
        SeleniumUtils.textIsPresentOnPage(driver,"Contenido");
    }
    // Prueba31: Utilizando un acceso vía URL u otra alternativa, tratar de acceder al perfil de un usuario que no sea amigo del usuario identificado en sesión. Comprobar que el sistema da un error de autorización.
    @Order(31)
    @Test
    @Transactional

    void Prueba31(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");
        //ir a ver amigos

        driver.navigate().to("http://localhost:8090/post/friendList/14");

        //No nos deja ver los datos del usuario 14, no es amigo
        SeleniumUtils.textIsNotPresentOnPage(driver, "user14@email.com");
        List<WebElement> errorMessage = PO_ErrorView.getText(driver,PO_Properties.getSPANISH());
        Assertions.assertEquals(errorMessage.get(0).getText(),PO_View.getP().getString("Error.page.notFound",PO_Properties.getSPANISH()));
    }
    // Prueba32: Visualizar al menos tres páginas alternando entre español e inglés (o el tercer idioma), comprobando que algunas etiquetas y textos cambian al idioma correspondiente. Ejemplo: Página principal, Opciones Principales de Usuario, Listado de Usuarios.
    @Order(32)
    @Test
    @Transactional

    void Prueba32(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("login.Identificate",PO_Properties.getSPANISH()));
        driver.findElement(By.id("btnLanguage")).click();
        driver.findElement(By.id("btnEnglish")).click();
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("login.Identificate",PO_Properties.getENGLISH()));
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("home.msg.private",PO_Properties.getENGLISH()));
        driver.findElement(By.id("btnLanguage")).click();
        driver.findElement(By.id("btnSpanish")).click();
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("home.msg.private",PO_Properties.getSPANISH()));
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "postAdd");
        PO_HomeView.clickOptionById(driver, "postAdd", "text", PO_View.getP().getString("posts.publish",PO_Properties.getSPANISH()));
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("nav.posts.create",PO_Properties.getSPANISH()));
        driver.findElement(By.id("btnLanguage")).click();
        driver.findElement(By.id("btnEnglish")).click();
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("nav.posts.create",PO_Properties.getENGLISH()));
    }
    // Prueba33: Visualizar al menos tres páginas alternando entre inglés y el tercer idioma elegido, comprobando que algunas etiquetas y textos cambian al idioma correspondiente. Ejemplo: Página principal, Opciones Principales de Usuario, Listado de Usuarios.
    @Order(33)
    @Test
    @Transactional

    void Prueba33(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        driver.findElement(By.id("btnLanguage")).click();
        driver.findElement(By.id("btnEnglish")).click();
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("login.Identificate",PO_Properties.getENGLISH()));
        driver.findElement(By.id("btnLanguage")).click();
        driver.findElement(By.id("btnFrance")).click();
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("login.Identificate",PO_Properties.getFRENCH()));
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("home.msg.private",PO_Properties.getFRENCH()));
        driver.findElement(By.id("btnLanguage")).click();
        driver.findElement(By.id("btnEnglish")).click();
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("home.msg.private",PO_Properties.getENGLISH()));
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "postAdd");
        PO_HomeView.clickOptionById(driver, "postAdd", "text", PO_View.getP().getString("posts.publish",PO_Properties.getENGLISH()));
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("nav.posts.create",PO_Properties.getENGLISH()));
        driver.findElement(By.id("btnLanguage")).click();
        driver.findElement(By.id("btnFrance")).click();
        SeleniumUtils.textIsPresentOnPage(driver,PO_View.getP().getString("nav.posts.create",PO_Properties.getFRENCH()));
    }
    // Prueba 34: Intenta acceder sin estar autenticado a la lista de usuarios. Nos devuelve a login
    @Order(34)
    @Test
    @Transactional

    void Prueba34(){
        // Accedemos a la página por url
        PO_LoginView.go(driver);
        // Obtenemos el mensaje de Identificate de la página de Login
        List<WebElement> welcomeMessage = PO_LoginView.getWelcomeMessage(driver,PO_Properties.getSPANISH());
        // Comprobamos que exista en la página actual
        Assertions.assertEquals(welcomeMessage.get(0).getText(),PO_View.getP().getString("login.Identificate",PO_Properties.getSPANISH()));
    }
    // Prueba 35: Intenta acceder sin estar autenticado a la lista de peticiones de amistad . Nos redirige a la página de Login
    @Order(35)
    @Test
    @Transactional

    void Prueba35(){
        // Accedemos a la lista de peticiones por url
        PO_FriendsView.goInvite(driver);
        // Obtenemos el mensaje de Identificate de la página de Login
        List<WebElement> welcomeMessage = PO_LoginView.getWelcomeMessage(driver,PO_Properties.getSPANISH());
        // Comprobamos que exista en la página actual
        Assertions.assertEquals(welcomeMessage.get(0).getText(),PO_View.getP().getString("login.Identificate",PO_Properties.getSPANISH()));
    }
    // Prueba 36: Estando autenticado como usuario intentamos acceder a logs/list que es como administrador y debe aparecer la página de Error
    @Order(36)
    @Test
    @Transactional

    void Prueba36(){
        // Navega a la página de inicio y haz clic en el botón de login
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");

        // Completa el formulario de inicio de sesión con credenciales válidas
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");

        PO_LogsView.go(driver);
        List<WebElement> errorMessage = PO_ErrorView.getText(driver,PO_Properties.getSPANISH());
        Assertions.assertEquals(errorMessage.get(0).getText(),PO_View.getP().getString("Error.page.notFound",PO_Properties.getSPANISH()));
    }
    // Prueba37: Estando autenticado como usuario administrador, visualizar todos los logs generados en una serie de interacciones. Esta prueba deberá generar al menos dos interacciones de cada tipo y comprobar que el listado incluye los logs correspondientes.
    @Order(37)
    @Test
    @Transactional

    void Prueba37(){
        PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
        PO_SignUpView.fillForm(driver, "pruebalogsALTA@gmail.com", "alta", "ALTA", "a123456789A+", "a123456789A+");
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user0@email.com", "Us3r@6-PASSW");
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_LogsView.go(driver);
        List<WebElement> loginLogs = PO_LogsView.getLogsList(driver,"LOGIN_EX");
        SeleniumUtils.textIsPresentOnPage(driver,"Login correcto por el usuario: admin@email.com");
        loginLogs = PO_LogsView.getLogsList(driver,"ALTA");
        SeleniumUtils.textIsPresentOnPage(driver,"pruebalogsALTA@gmail.com");
        loginLogs = PO_LogsView.getLogsList(driver,"LOGOUT");
        SeleniumUtils.textIsPresentOnPage(driver,"pruebalogsALTA@gmail.com");
        loginLogs = PO_LogsView.getLogsList(driver,"LOGOUT");
        SeleniumUtils.textIsPresentOnPage(driver,"pruebalogsALTA@gmail.com");
    }
    // Prueba38: Estando autenticado como usuario administrador, ir a visualización de logs y filtrar por un tipo, pulsar el botón/enlace borrar logs y comprobar que se eliminan los logs del tipo seleccionado de la base de datos.
    @Order(38)
    @Test
    @Transactional

    void Prueba38(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        PO_LogsView.go(driver);
        List<WebElement> loginLogs = PO_LogsView.getLogsList(driver,"LOGIN_EX");
        WebElement deleteButton = driver.findElement(By.id("deleteButton"));
        deleteButton.click();
        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        assertEquals(0, filas.size());
        List<WebElement> PETloginLogs = PO_LogsView.getLogsList(driver,"PET");
        WebElement deleteButton2 = driver.findElement(By.id("deleteButton"));
        deleteButton2.click();
        List<WebElement> newfilas = SeleniumUtils.waitLoadElementsByXpath(driver, "//tbody/tr", 6);
        assertEquals(1, newfilas.size());
    }
    // Prueba39: Recomendar Publicación de un Amigo
    @Order(39)
    @Test
    @Transactional

    void Prueba39(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");
        //Vamos al listado de amigos
        PO_HomeView.clickOptionById(driver, "navbarDropdown", "id", "friendsList");
        PO_HomeView.clickOptionById(driver, "friendsList", "text", "Lista de amigos");
        //Vamos a los post de nuestro primer amigo
        WebElement friend = driver.findElement(By.xpath("//tbody/tr[1]/td[2]/a"));
        friend.click();
        //Recomendamos su primera publicación
        WebElement recommend = driver.findElement(By.xpath("//tbody/tr[1]/td[7]/a"));
        recommend.click();
        //Comprobamos que se han incrementado las recomendaciones y ocultado el boton recomendar
        String numberRecommendations = driver.findElement(By.xpath("//tbody/tr[1]/td[6]")).getText();
        Assertions.assertEquals("1", numberRecommendations);
        assertThrows(StaleElementReferenceException.class, friend::click); //Excepción al no encontrar el botón
    }
    // Prueba40: Intento de Recomendar Publicación de un No-Amigo
    @Order(40)
    @Test
    @Transactional

    void Prueba40(){
        PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
        PO_LoginView.fillForm(driver, "user03@email.com", "Us3r@3-PASSW");
        //Intentamos recomendar una publicación de un usuario que no es amigo de user03
        driver.navigate().to("http://localhost:8090/recommendation/27");
        //Comprobamos que nos denega
        List<WebElement> errorMessage = PO_ErrorRecommendation.getText(driver, PO_Properties.getSPANISH());
        Assertions.assertEquals(errorMessage.get(0).getText(), PO_View.getP().getString("Error.page.recommendation",PO_Properties.getSPANISH()));
    }

    // Prueba41: Cambiar Estado de una Publicación
    @Test
    @Order(41)
    @Transactional

    void Prueba41() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        //login como user1
        PO_Login.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");

        //ir al listado de publicaicones
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "listPostAll");

        PO_HomeView.clickOptionById(driver, "listPostAll", "text", PO_View.getP().getString("posts.title.head",PO_Properties.getSPANISH()));


        //guardar el estado de la publicacion de la 1 fila antes de cambiarlo
        WebElement estado = driver.findElement(By.xpath("//tbody/tr[1]/td[5]"));
        String estadoAnterior=estado.getText();
        //modificar una publicacion la 1
        WebElement btnModificar = driver.findElement(By.xpath("//tbody/tr[1]/td[8]/a"));
        btnModificar.click();

        //cambiamos la opcion por otra
        WebElement selectElement = driver.findElement(By.xpath("//select[@id='status']"));
        Select select = new Select(selectElement);
        // Selecciona el segundo elemento del combobox
        select.selectByIndex(1);  // El índice 0 es el primer elemento

        // Localiza el botón de tipo "submit" y envía el formulario
        WebElement submitButton = driver.findElement(By.xpath("/html/body/div/form/div[2]/div/button"));
        submitButton.click();

        //comprobar que se cambio por otro

        WebElement nuevoEstado=driver.findElement(By.xpath("//tbody/tr[1]/td[5]"));
        Assertions.assertNotSame(estadoAnterior, nuevoEstado.getText(), "El estado de la publicacion no ha cambiado");
    }
    // Prueba42: Verificación de Publicación Censurada por Usuario Estándar
    @Test
    @Order(42)
    @Transactional

    void Prueba42() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        //login como user1
        PO_Login.fillForm(driver, "user13@email.com", "Us3r@13-PASSW");

        //ir al listado de publicaicones
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "ownedPosts");

        PO_HomeView.clickOptionById(driver, "ownedPosts", "text", PO_View.getP().getString("posts.title.head",PO_Properties.getSPANISH()));


        // Encontrar todas las filas de la tabla
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='postListTable']/tbody/tr"));

        // Asegurarse de que haya exactamente 5 filas y de que el estado sea Aceptada o Moderada e que el
        Assertions.assertEquals(5, rows.size(), "La tabla debe tener dos filas");

        for(int i=0;i<rows.size(); i++){
            WebElement estado = driver.findElement(By.xpath("//*[@id='postListTable']/tbody/tr["+(i+1)+"]/td[5]"));
            assertTrue(estado.getText().equals("Aceptada") || estado.getText().equals("Moderada"), "El estado de la publicacion no es Aceptada o Moderada");
        }


    }

    // Prueba43: Verificación de Publicación Moderada por Usuario Estándar
    @Test
    @Order(43)
    @Transactional

    void Prueba43() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        //login como user1
        PO_Login.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");

        //ir a la opcion de gestion de amistades y luego a ver amigos
        PO_HomeView.clickOptionById(driver, "navbarDropdown", "id", "friendsList");

        PO_HomeView.clickOptionById(driver, "friendsList", "text", "Lista de amigos");

        //click en el segundo amigo el user 2
        //html/body/div/div/table/tbody/tr[1]/td[2]/a
        WebElement link = driver.findElement(By.xpath("//*[@id='friendsTable']/tbody/tr[1]/td[2]/a"));
        link.click();

        //en la vista de la tabla de amigo compruebas que solamante aparezcan Aceptadas

        //obtenemos la tabla de publicaciones
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='postListTable']/tbody/tr"));

        // Asegurarse de que haya exactamente 5 filas y de que el estado sea Aceptada o Moderada e que el
        Assertions.assertEquals(5, rows.size(), "La tabla debe tener cinco filas");

        for(int i=0;i<rows.size(); i++){
            WebElement estado = driver.findElement(By.xpath("//*[@id='postListTable']/tbody/tr["+(i+1)+"]/td[5]"));
            assertTrue(estado.getText().equals("Aceptada") , "El estado de la publicacion no es Aceptada ");
        }


    }
    // Prueba44: Acceso a Cambio de Estado de Publicación por Usuario Estándar
    @Test
    @Order(44)
    @Transactional

    void Prueba44() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        PO_Login.fillForm(driver, "user01@email.com", "Us3r@1-PASSW");

        //intentas acceder a la vista de cambiar algun status de publicaciones

        driver.get("http://localhost:8090/post/edit/29");

        //te redijie al loggin

        String checkText = PO_View.getP().getString("login.message",PO_Properties.getSPANISH());
        SeleniumUtils.textIsPresentOnPage(driver, checkText);



    }
    // Prueba45: Búsqueda con Campo Vacío
    @Test
    @Order(45)
    @Transactional

    void Prueba45() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        //login como user1
        PO_Login.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");

        //ir al listado de publicaicones
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "listPostAll");

        PO_HomeView.clickOptionById(driver, "ownedPosts", "text", PO_View.getP().getString("posts.title.head",PO_Properties.getSPANISH()));


        //guardar el num de filas de la lista //*[@id="postListTable"]
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='postListTable']/tbody/tr"));
        int filas1=rows.size();

        //buscar con un campo vacio
        WebElement barraBusqueda= driver.findElement(By.id("searchTextPost"));
        barraBusqueda.sendKeys("");
        //darle al boton de buscar
        WebElement botonBuscar=driver.findElement(By.id("searchButton"));

        botonBuscar.click();
        //sacar las filas otra vez

        //guardar el num de filas de la lista
        rows= driver.findElements(By.xpath("//*[@id='postListTable']/tbody/tr"));

        Assertions.assertEquals(filas1, rows.size(), "Si buscas vacio no sale todo el listado ");


    }
    // Prueba46: Búsqueda con Texto Inexistente
    @Test
    @Order(46)
    @Transactional

    void Prueba46() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        //login como user1
        PO_Login.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");

        //ir al listado de publicaicones
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "listPostAll");

        PO_HomeView.clickOptionById(driver, "ownedPosts", "text", PO_View.getP().getString("posts.title.head",PO_Properties.getSPANISH()));


        //guardar el num de filas de la lista //*[@id="postListTable"]
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='postListTable']/tbody/tr"));


        //buscar con un campo vacio
        WebElement barraBusqueda= driver.findElement(By.id("searchTextPost"));
        barraBusqueda.sendKeys("estecamponoexisterianunca");
        //darle al boton de buscar
        WebElement botonBuscar=driver.findElement(By.id("searchButton"));

        botonBuscar.click();
        //sacar las filas otra vez

        //guardar el num de filas de la lista
        rows= driver.findElements(By.xpath("//*[@id='postListTable']/tbody/tr"));

        //se espearn 0 filas
        Assertions.assertEquals(0, rows.size(), "se encontraron resultados ");


    }
    // Prueba47: Búsqueda de Publicaciones Censuradas
    @Test
    @Order(47)
    @Transactional

    void Prueba47() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        //login como admin
        PO_Login.fillForm(driver, "admin@email.com", "@Dm1n1str@D0r");

        //ir al listado de publicaicones
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "listPostAll");

        PO_HomeView.clickOptionById(driver, "listPostAll", "text", PO_View.getP().getString("posts.title.head",PO_Properties.getSPANISH()));


        //guardar el num de filas de la lista //*[@id="postListTable"]
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='postListTable']/tbody/tr"));


        //buscar con un campo
        WebElement barraBusqueda= driver.findElement(By.id("searchTextPost"));
        barraBusqueda.sendKeys("Censurada");
        //darle al boton de buscar
        WebElement botonBuscar=driver.findElement(By.id("searchButton"));

        botonBuscar.click();
        //sacar las filas otra vez

        //guardar el num de filas de la lista
        rows= driver.findElements(By.xpath("//*[@id='postListTable']/tbody/tr"));

        //se espearn 3 filas DE CENSURADAS
        Assertions.assertTrue( rows.size()>0, "se encontraron resultados ");


        //te deslogeas y pruebas con otro
        PO_HomeView.clickOption(driver, "logout", "class", "btn btn-primary");



    }
    // Prueba48: Crear Publicación con Foto Adjunta
    @Test
    @Order(48)
    @Transactional

    void Prueba48() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        //login como user1
        PO_Login.fillForm(driver, "user16@email.com", "Us3r@16-PASSW");

        // Haz clic en la opción "Crear publicación" en el menú de navegación
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "postAdd");
        PO_HomeView.clickOptionById(driver, "postAdd", "text", PO_View.getP().getString("posts.publish",PO_Properties.getSPANISH()));
        // Espera a que se cargue la página de creación de publicación
        SeleniumUtils.waitLoadElementsBy(driver, "id", "title", 1);

        // Rellena el formulario de creación de publicación
        WebElement titleInput = driver.findElement(By.id("title"));
        titleInput.sendKeys("Esto es una prueba");

        WebElement contentInput = driver.findElement(By.id("content"));
        contentInput.sendKeys("Contenido de la publicación");

        // Obtén la ruta del proyecto
        String projectPath = System.getProperty("user.dir");

        // Ruta relativa al archivo en la carpeta static
        String filePath = "src\\main\\resources\\static\\images\\testImage.png";

        // Ruta absoluta al archivo
        String absolutePath = projectPath + "\\" + filePath;

        //seleccionar la foto
        WebElement fileInput = driver.findElement(By.xpath("//input[@type='file']"));
        fileInput.sendKeys(absolutePath);



        // Haz clic en el botón "Publicar"
        WebElement publishButton = driver.findElement(By.xpath("//button[@type='submit']"));
        publishButton.click();

        //ir a ver las publicaiones


        // Haz clic en la opción "Listado de  publicaciónes " en el menú de navegación
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "ownedPosts");
        PO_HomeView.clickOptionById(driver, "ownedPosts", "text", PO_View.getP().getString("posts.title.head",PO_Properties.getSPANISH()));

        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertTrue( filas.size()>0, "se encontraron resultados ");
        SeleniumUtils.textIsPresentOnPage(driver,"Esto es una prueba");

        SeleniumUtils.textIsNotPresentOnPage(driver,PO_View.getP().getString("Error.loadImage",PO_Properties.getSPANISH()));

    }
    // Prueba49: Crear Publicación sin Foto Adjunta
    @Test
    @Order(49)
    @Transactional

    void Prueba49() {
        //usar la navbar para ir al loggin
        PO_NavBar.clickOption(driver, "login", "class", "btn btn-primary");
        //login como admin
        PO_Login.fillForm(driver, "user16@email.com", "Us3r@16-PASSW");

        // Haz clic en la opción "Crear publicación" en el menú de navegación
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "postAdd");
        PO_HomeView.clickOptionById(driver, "postAdd", "text", PO_View.getP().getString("posts.publish",PO_Properties.getSPANISH()));

        // Espera a que se cargue la página de creación de publicación
        SeleniumUtils.waitLoadElementsBy(driver, "id", "title", 1);

        // Rellena el formulario de creación de publicación
        WebElement titleInput = driver.findElement(By.id("title"));
        titleInput.sendKeys("Esto es una prueba");

        WebElement contentInput = driver.findElement(By.id("content"));
        contentInput.sendKeys("Contenido de la publicación");

        // Haz clic en el botón "Publicar"
        WebElement publishButton = driver.findElement(By.xpath("//button[@type='submit']"));
        publishButton.click();

        //ir al listado de publicaicones y ver que tienes la miagen por defect


        // Haz clic en la opción "Listado de  publicaciónes " en el menú de navegación
        PO_HomeView.clickOptionById(driver, "postDropdown", "id", "ownedPosts");
        PO_HomeView.clickOptionById(driver, "ownedPosts", "text", PO_View.getP().getString("posts.title.head",PO_Properties.getSPANISH()));

        List<WebElement> filas = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertTrue( filas.size()>0, "se encontraron resultados ");
        SeleniumUtils.textIsPresentOnPage(driver,"Esto es una prueba");
        SeleniumUtils.textIsNotPresentOnPage(driver,PO_View.getP().getString("Error.loadImage",PO_Properties.getSPANISH()));

    }

}
