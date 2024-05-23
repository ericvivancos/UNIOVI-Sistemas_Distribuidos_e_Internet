package com.uniovi.sdi2223entrega2test.n;

import com.uniovi.sdi2223entrega2test.n.pageobjects.*;
import groovy.transform.AutoExternalize;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.uniovi.sdi2223entrega2test.n.util.SeleniumUtils;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    static String PathFirefox =  "C://Program Files//Mozilla Firefox//firefox.exe";
    static String Geckodriver = "src/test/java/com/uniovi/sdi2223entrega2test/geckodriver-v0.30.0-win64.exe";

    static String loginApiURl="http://localhost:8081/api/v1.0/users/login";
    static String getFriendsApiURl="http://localhost:8081/api/v1.0/chats/getFriends";
    static String getChatsApiURl="http://localhost:8081/api/v1.0/chats/getChats";
    static String sendChatMessageURL = "http://localhost:8081/api/v1.0/chats/sendMessage/";
    static String getMessagesApiUrl = "http://localhost:8081/api/v1.0/chats/getMessages/";

    static String deleteChatApiUrl = "http://localhost:8081/api/v1.0/chats/deleteChat/";

    static String readMessageApiUrl = "http://localhost:8081/api/v1.0/chats/markAsRead/";

    //static String PathFirefox = "/Applications/Firefox.app/Contents/MacOS/firefox-bin";
//static String Geckodriver = "/Users/USUARIO/selenium/geckodriver-v0.30.0-macos";
//Común a Windows y a MACOSX
    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    static String URL = "http://localhost:8081";
    static String URL_CLIENTE = "http://localhost:8081/apiclient/client.html?w=login";


    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    @BeforeEach
    public void setUp() {
        driver.navigate().to(URL);
    }

    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    //Antes de la primera prueba
    @BeforeAll
    static public void begin() {
        RestAssured.get("http://localhost:8081/test/reset");

    }

    //Al finalizar la última prueba
    @AfterAll
    static public void end() {
        RestAssured.get("http://localhost:8081/test/reset");
        ///Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }
    //resetear la bd tras cada test
    @AfterEach
    void resetBD(){
        RestAssured.get("http://localhost:8081/test/reset");
        driver.navigate().to(URL);
    }
    @Test
    @Order(1)
    void PR01() {

        PO_HomeView.clickRegister(driver);
        PO_SignUpView.fillForm(driver, "testUser", "Julio", "01/05/2002","testingUserAdd@email.com", "a123456789A+?08213", "a123456789A+?08213");
        String checkText = "Identificación de usuario";
        PO_View.checkElementBy(driver, "text", checkText);
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }
    //datos invalidos testear los errores
    @Test
    @Order(2)
    public void PR02() {
        // Navega a la página de registro
        PO_HomeView.clickRegister(driver);

        // Intenta llenar el formulario de registro con datos inválidos (vacíos)
        PO_SignUpView.fillForm(driver, "", "", "","", "", "");

        // Verifica que se muestra un mensaje de error
        int numErrores = 6;
        //comprobar que la lista de errores contiene 6 elementos
        List<WebElement> elementos = PO_View.checkElementBy(driver, "id", "errorList");
        assertEquals(numErrores, elementos.size());
        }
    //mal puesta la 2 contra repetida
    @Test
    @Order(3)
    void PR03() {

        PO_HomeView.clickRegister(driver);
        PO_SignUpView.fillForm(driver, "testUser", "Julio", "01/05/2002","testingUserAddNotExistThisEmail@email.com", "a123456789A+?08213", "a123456789A+?08213dsadaewdasdasd");
        // Verifica que se muestra un mensaje de error
        int numErrores = 1;
        //comprobar que la lista de errores contiene 6 elementos
        List<WebElement> elementos = PO_View.checkElementBy(driver, "id", "errorList");
        assertEquals(numErrores, elementos.size());
        SeleniumUtils.textIsPresentOnPage(driver, "Las contraseñas no coinciden");

    }
    //email ya existente usamos el de un usuario que exista ejemplo el admin
    @Test
    @Order(4)
    public void PR04() {

        PO_HomeView.clickRegister(driver);
        PO_SignUpView.fillForm(driver, "testUser", "Julio", "01/05/2002","admin@email.com", "a123456789A+?08213", "a123456789A+?08213");
        String checkText = "El email ya está registrado";
        int numErrores = 1;
        //comprobar que la lista de errores contiene 6 elementos
        List<WebElement> elementos = PO_View.checkElementBy(driver, "id", "errorList");

        assertEquals(numErrores, elementos.size());
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }
    //inicio de session administrador ver listado de usuarios
    @Test
    @Order(5)
    public void PR05() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        String checkText = "Listado de usuarios del sistema";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }
    //incio sesion estandar ver listado de usuario de la red social
    @Test
    @Order(6)
    public void PR06() {

        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }
    //loggin fallido usuario 1 contra incorrecta
    @Test
    @Order(7)
    public void PR07() {

        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "ErrorDecontra-?1;");
        int numErrores = 1;
        //comprobar que la lista de errores contiene 6 elementos
        List<WebElement> elementos = PO_View.checkElementBy(driver, "id", "errorList");

        assertEquals(numErrores, elementos.size());
        String checkText = "Identificación de usuario";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }
    //loggin campos vacios
    @Test
    @Order(8)
    public void PR08() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "", "");
        int numErrores = 2;
        //comprobar que la lista de errores contiene 2 errores indicando que los campos son obligatorios
        List<WebElement> elementos = PO_View.checkElementBy(driver, "id", "errorList");

        assertEquals(numErrores, elementos.size());
        String checkText = "Identificación de usuario";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        //probar con un campo vacio solamente
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "");
        numErrores = 2;
        //comprobar que la lista de errores contiene 2 errores indicando que los campos son obligatorios
        elementos = PO_View.checkElementBy(driver, "id", "errorList");

        assertEquals(numErrores, elementos.size());
        checkText = "Identificación de usuario";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }
    //click en cerras sesion y comprobar que se muestra se ha cerrado sessioncorrectamente
    @Test
    @Order(9)
    public void PR09() {


        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        PO_NavView.logout(driver);
        //compropbar el texto
        int sucess = 1;
        //comprobar que la lista de errores contiene 2 errores indicando que los campos son obligatorios
        List<WebElement> elementos = PO_View.checkElementBy(driver, "id", "successList");
        assertEquals(sucess, elementos.size());
        checkText = "Has cerrado sesión correctamente";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

    }
    //comprobar que el boton de logout no es visible para un usuario no logeado
    @Test
    @Order(10)
    public void PR10() {
        // Ir al index /
        driver.navigate().to(URL);

        // Comprobar que no está el botón de logout
        List<WebElement> logoutElements = driver.findElements(By.id("logoutNavBar"));
        assertTrue(logoutElements.isEmpty());

        // Loguearse y ver que sí sale
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        // Comprobar que está el botón de logout
        logoutElements = driver.findElements(By.id("logoutNavBar"));
        Assertions.assertFalse(logoutElements.isEmpty());
    }


    /* test comprobar el listado de usuario y ademas que todos los usuarios tienen los campos */
    @Test
    @Order(11)
    public void PR11() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        // Obtén el número de filas en la tabla

        for( int pagina=1;pagina<5;pagina++) {


            // Iterar sobre cada fila
            List<WebElement> rows = driver.findElements(By.tagName("tr"));
            rows.remove(0);
            for (WebElement row : rows) {
                List<WebElement> columns = row.findElements(By.tagName("td"));
                Assertions.assertNotNull(columns.get(1).getText());
                Assertions.assertNotNull(columns.get(2).getText());
                Assertions.assertNotNull(columns.get(3).getText());
            }

            String pageUrl = "/listAllUsers?page=" + pagina+"&limit=10";
            WebElement nextPageLink = driver.findElement(By.xpath("//a[@class='page-link' and @href='" + pageUrl + "']"));

            nextPageLink.click();
        }


    }
    /*
    Autenticarse como administrador, editar un usuario estándar, cambiando su rol a
    administrador, email, nombre y apellidos, comprobar que los datos se han actualizados y probar que el nuevo sea admin
     */
    @Test
    @Order(12)
    public void PR12() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //editar el primer usuario de la tabla eric vicancos
        WebElement editButton = driver.findElement(By.linkText("Modificar"));
        editButton.click();
        //comprobar que se ha cargado la pagina de edicion
        checkText = "Editar usuario";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //editar los campos del formulario y enviarlo para probarlo
        PO_EditUserView.fillForm(driver, "emailEditado@email.com", "nombreEditado", "apellidoEditado",2);

        //comprobar que se ha actualizado el usuario
        String[] camposEditados = {"emailEditado@email.com", "nombreEditado", "apellidoEditado"};


            // Encuentra la celda y obtén su texto
            String cellText = driver.findElement(By.className("email")).getText();
            // Compara el texto de la celda con los datos editados
            Assertions.assertEquals(camposEditados[0], cellText);


        //entrar como usuario editado
        PO_NavView.logout(driver);
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, camposEditados[0],"Us3r@1-PASSW");
        checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //comprobar que el usuario editado es admin y por ello ptiene el boton editar y puede editar
        WebElement editButton2 = driver.findElement(By.linkText("Modificar"));
        Assertions.assertTrue(editButton2.isDisplayed());

        //click en el boton para comprobar que va sa editar
        editButton2.click();
        checkText = "Editar usuario";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);


    }
    //editar usuario datos invalidos email ya existen por otro user , cnombre vacio y surname vacio
    @Test
    @Order(13)
    public void PR13() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //editar el primer usuario de la tabla eric vicancos
        WebElement editButton =  driver.findElement(By.linkText("Modificar"));
        editButton.click();
        //comprobar que se ha cargado la pagina de edicion
        checkText = "Editar usuario";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //editar los campos del formulario y enviarlo para probarlo
        PO_EditUserView.fillForm(driver, "admin@email.com", "", "", 2);

        //se espera que esten en la mism a pagina pero con errores
        checkText = "Editar usuario";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //ver los errores
        int numErrores = 3;
        //comprobar que la lista de errores contiene 3 errores
        List<WebElement> elementos = PO_View.checkElementBy(driver, "id", "errorList");
        Assertions.assertEquals(numErrores, elementos.size());

        //comprobar que el usuario no se edito desde la vista de Listall
        driver.navigate().to(URL + "/listAllUsers");
        //comprobar que se ha actualizado el usuario
        String[] camposEditados = {"admin@email.com", "", ""};

        for(int i=1; i<=3; i++){
            // Construye el XPath para la celda actual
            String cellText = driver.findElement(By.className("email")).getText();
            // Compara el texto de la celda con los datos editados
            Assertions.assertNotEquals(camposEditados[i-1], cellText);
        }

    }
    /* test borrar el primer usuario de la lista */
    @Test
    @Order(14)
    public void PR14() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        WebElement checkboxSel = driver.findElement(By.cssSelector("tr:first-child input"));
        checkboxSel.click();

        SeleniumUtils.clickElementBy(driver, By.id("btn-delete"));

        SeleniumUtils.textIsNotPresentOnPage(driver, "uo303984@uniovi.es");

    }
    //Comprobar que se elimina el último usuario
    @Test
    @Order(15)
    public void PR15() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        for( int pagina=1;pagina<5;pagina++) {
            // Encuentra el boton de siguiente pagina y le da click y asi compuebas que nignun usuario tiene campos vacios
            WebElement nextPageLink = driver.findElement(By.linkText(""+pagina+""));
            nextPageLink.click();
        }

        WebElement checkboxSel = driver.findElement(By.cssSelector("tr:last-child input"));
        checkboxSel.click();

        SeleniumUtils.clickElementBy(driver, By.id("btn-delete"));

        for( int pagina=1;pagina<5;pagina++) {
            // Encuentra el boton de siguiente pagina y le da click y asi compuebas que nignun usuario tiene campos vacios
            WebElement nextPageLink = driver.findElement(By.linkText(""+pagina+""));
            nextPageLink.click();
        }

        SeleniumUtils.textIsNotPresentOnPage(driver, "prueba42@email.com");

    }

    @Test
    @Order(16)
    public void PR16() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        WebElement checkboxSel1 = driver.findElement(By.cssSelector("tr:nth-child(1) input"));
        checkboxSel1.click();
        WebElement checkboxSel2 = driver.findElement(By.cssSelector("tr:nth-child(2) input"));
        checkboxSel2.click();
        WebElement checkboxSel3 = driver.findElement(By.cssSelector("tr:nth-child(3) input"));
        checkboxSel3.click();

        SeleniumUtils.clickElementBy(driver, By.id("btn-delete"));

        SeleniumUtils.textIsNotPresentOnPage(driver, "uo303984@uniovi.es");
        SeleniumUtils.textIsNotPresentOnPage(driver, "user01@email.com");
        SeleniumUtils.textIsNotPresentOnPage(driver, "user02@email.com");
        SeleniumUtils.textIsPresentOnPage(driver, "user05@email.com");
        SeleniumUtils.textIsPresentOnPage(driver, "user06@email.com");
        SeleniumUtils.textIsPresentOnPage(driver, "user07@email.com");

    }

    /* test comprobar que el admin no puede eliminarse a sí mismo */
    @Test
    @Order(17)
    public void PR17() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        for( int pagina=1;pagina<5;pagina++) {
            // Encuentra el boton de siguiente pagina y le da click y asi compuebas que nignun usuario tiene campos vacios
            WebElement nextPageLink = driver.findElement(By.linkText(""+pagina+""));
            nextPageLink.click();
        }

        WebElement checkboxSel1 = driver.findElement(By.cssSelector(" tr:nth-child(2) input"));
        checkboxSel1.click();

        SeleniumUtils.clickElementBy(driver, By.id("btn-delete"));
        SeleniumUtils.textIsPresentOnPage(driver,"No puedes eliminarte a ti mismo");
        for( int pagina=1;pagina<5;pagina++) {
            // Encuentra el boton de siguiente pagina y le da click y asi compuebas que nignun usuario tiene campos vacios
            WebElement nextPageLink = driver.findElement(By.linkText(""+pagina+""));
            nextPageLink.click();
        }

        String checkText2 = "admin";
        SeleniumUtils.textIsPresentOnPage(driver, checkText2);
    }

    /* test comprobar el listado de usuario para usuario normal y ademas que todos los usuarios tienen los campos */
    @Test
    @Order(18)
    public void PR18() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        SeleniumUtils.textIsNotPresentOnPage(driver, "admin");
        // Obtén el número de filas en la tabla

        for( int pagina=1;pagina<5;pagina++) {
            List<WebElement> rows = driver.findElements(By.tagName("tr"));
            rows.remove(0);
            for (WebElement row : rows) {
                List<WebElement> columns = row.findElements(By.tagName("td"));
                Assertions.assertNotNull(columns.get(1).getText());
                Assertions.assertNotNull(columns.get(2).getText());
                Assertions.assertNotNull(columns.get(3).getText());
            }

            // Encuentra el boton de siguiente pagina y le da click y asi compuebas que nignun usuario tiene campos vacios
            String pageUrl = "/listUsersSocialMedia?page=" + pagina+"&limit=10";
            WebElement nextPageLink = driver.findElement(By.xpath("//a[@class='page-link' and @href='" + pageUrl + "']"));

            nextPageLink.click();
        }


    }

    // Buscar con el campo de texto vacio
    @Test
    @Order(19)
    public void PR19() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        SeleniumUtils.textIsNotPresentOnPage(driver, "admin");

        SeleniumUtils.clickElementBy(driver, By.id("btn-search"));

        // Obtén el número de filas en la tabla
        List<WebElement> rows = driver.findElements(By.xpath("/html/body/div/div[1]/table/tbody/tr"));
        int rowCount = rows.size();

        for( int pagina=1;pagina<5;pagina++) {


            // Iterar sobre cada fila
            for (int i = 1; i <= rowCount; i++) {
                // Itera sobre las columnas
                for (int j = 1; j <= 3; j++) {
                    // Construye el XPath para la celda actual
                    String cellXPath = "/html/body/div/div[1]/table/tbody/tr[" + i + "]/td[" + j + "]";
                    // Encuentra la celda y obtén su texto
                    String cellText = driver.findElement(By.xpath(cellXPath)).getText();
                    // Verifica que el texto de la celda no esté vacío
                    Assertions.assertFalse(cellText.isEmpty());
                }

            }
            // Encuentra el boton de siguiente pagina y le da click y asi compuebas que nignun usuario tiene campos vacios
            WebElement nextPageLink = driver.findElement(By.xpath("//a[@class='page-link' and @href='/listUsersSocialMedia?search=&page=" + pagina + "&limit=10']"));
            nextPageLink.click();
        }


    }

    //Buscar algo que no exista
    @Test
    @Order(20)
    public void PR20() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        SeleniumUtils.textIsNotPresentOnPage(driver, "admin");

        WebElement search = driver.findElement(By.id("search"));
        search.click();
        search.clear();
        search.sendKeys("NO_EXISTE");
        SeleniumUtils.clickElementBy(driver, By.id("btn-search"));

        // Obtén el número de filas en la tabla
        List<WebElement> rows = driver.findElements(By.xpath("/html/body/div/div[1]/table/tbody/tr"));
        int rowCount = rows.size();
        Assertions.assertEquals(0, rowCount);
    }

    //Buscar algo que exista
    @Test
    @Order(21)
    public void PR21() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        SeleniumUtils.textIsNotPresentOnPage(driver, "admin");

        WebElement search = driver.findElement(By.id("search"));
        search.click();
        search.clear();
        search.sendKeys("Elena");
        SeleniumUtils.clickElementBy(driver, By.id("btn-search"));

        // Obtén el número de filas en la tabla
        List<WebElement> rows = driver.findElements(By.cssSelector("#userList tr"));
        int rowCount = rows.size();
        Assertions.assertEquals(1, rowCount);
        SeleniumUtils.textIsPresentOnPage(driver, "user11@email.com");
    }

    //Comprobar que la solicitud de amistad se muestra en la lista de solicitudes de amistad al que fue enviada
    @Test
    @Order(22)
    public void PR22() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //Clicamos en el botón agregar amigo
        checkText = "Solicitud enviada";
        WebElement addFriendButton = driver.findElement(By.xpath("//tbody/tr[5]/td[5]/a"));
        addFriendButton.click();
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //Nos deslogueamos y nos logeamos con el usuario al que hemos mandado la solicitud
        PO_NavView.logout(driver);
        PO_LoginView.fillLoginForm(driver, "user03@email.com", "Us3r@3-PASSW");
        //Nos dirigimos a las solicitudes de amistad
        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text","Nombre del Remitente");
        //Comprobamos que hay una solicitud
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr"));
        Assertions.assertEquals(1, rows.size());
        String fechaXpath = String.format("//*[@id='friendRequestList']//tbody/tr[%d]/td[3]",1);
        WebElement fechaElement = driver.findElement(By.xpath(fechaXpath));
        // Asegurarse de que la celda de fecha no esté vacía
        Assertions.assertFalse(fechaElement.getText().isEmpty(), "La celda de fecha no debe estar vacía");
    }

    //Solictud de amistad ya enviada, si se vuelve a realizar otra, no lo procesará e informará al usuario
    @Test
    @Order(23)
    public void PR23() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //Clicamos en el botón agregar amigo
        checkText = "Solicitud enviada";
        WebElement addFriendButton = driver.findElement(By.xpath("//tbody/tr[1]/td[5]/a"));
        addFriendButton.click();
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //Volvemos a pulsar el mismo botón de agregar amigo para el mismo usuario
        addFriendButton = driver.findElement(By.xpath("//tbody/tr[1]/td[5]/a"));
        addFriendButton.click();
        checkText = "Ya existe una petición de amistad pendiente";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }

    //Comprobamos el listado con varias peticiones de amistad
    @Test
    @Order(24)
    public void PR24() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //Nos dirijimos a el menu donde visualizamos las solicitudes
        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text","Nombre del Remitente");
        //Comprobamos que haya solictudes de amistad
        int rowCount = 0;
        for( int pagina=1;pagina<4;pagina++) {
            // Encuentra el boton de siguiente pagina y le da click y asi compuebas que nignun usuario tiene campos vacios
            WebElement nextPageLink = driver.findElement(By.xpath("/html/body/div/div[2]/ul/li["+ pagina +"]/a"));
            nextPageLink.click();
            List<WebElement> rows = driver.findElements(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr"));
            rowCount = rows.size();

            // Iterar sobre cada fila
            for (int i = 1; i <= rowCount; i++) {
                String fechaXpath = String.format("//*[@id='friendRequestList']//tbody/tr[%d]/td[3]", i);
                String nombreXpath = String.format("//*[@id='friendRequestList']//tbody/tr[%d]/td[1]", i);
                WebElement fechaElement = driver.findElement(By.xpath(fechaXpath));
                WebElement nombreElement = driver.findElement(By.xpath(nombreXpath));

                // Asegurarse de que la celda de fecha y del nombre no estén vacías
                Assertions.assertFalse(fechaElement.getText().isEmpty(), "La celda de fecha no debe estar vacía");
                Assertions.assertFalse(nombreElement.getText().isEmpty(), "La celda de nombre no debe estar vacía");
            }

        }
    }

    /*
     Aceptamos una solicitud de amistad y comprobamos que tanto para el usuario que mando la solicutd
     como el que la acepto, se ha creado esa amistad.
     */
    @Test
    @Order(25)
    public void PR25() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        //Nos dirigimos a las solicitudes de amistad
        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text","Nombre del Remitente");
        //Pulsamos en el botón aceptar
        String email = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[5]/td[2]")).getText();
        WebElement acceptar = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[5]/td[4]/a"));
        acceptar.click();
        //Comprobamos que el usuario que ha enviado la envitación es nuestro amigo
        String friendEmail = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[1]/td[1]")).getText();
        Assertions.assertEquals(email, friendEmail);
        //Nos dirigimos a las solicitudes de amistad para comprobar que ha desaparecido la solicitud
        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text","Nombre del Remitente");
        email = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[1]/td[2]")).getText();
        Assertions.assertNotEquals(email, friendEmail);
        //Vamos a comprobar que de parte de la persona que mando la solicitud se le muestra la amistad
        PO_NavView.logout(driver);
        PO_LoginView.fillLoginForm(driver, friendEmail, "Us3r@4-PASSW");
        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendsList");
        PO_HomeView.clickOptionById(driver, "friendsList", "text","Nombre");
        String friendEmail2 = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[1]/td[1]")).getText();
        Assertions.assertEquals(friendEmail2, "user09@email.com");
    }
    @Test
    @Order(26)
    public void PR26() {
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        //Nos dirigimos a las solicitudes de amistad
        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text","Nombre del Remitente");
        //Pulsamos en el botón aceptar
        String email = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[5]/td[2]")).getText();
        WebElement acceptar = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[5]/td[4]/a"));
        acceptar.click();
        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text","Nombre del Remitente");
        String email2 = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[5]/td[2]")).getText();
        WebElement acceptar2 = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[5]/td[4]/a"));
        acceptar2.click();
        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text","Nombre del Remitente");
        String email3 = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[5]/td[2]")).getText();
        WebElement acceptar3 = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[5]/td[4]/a"));
        acceptar3.click();

        //Vamos a comprobar que de parte de la persona que mando la solicitud se le muestra la amistad

        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendsList");
        PO_HomeView.clickOptionById(driver, "friendsList", "text","Nombre");
        String friendEmail1 = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[1]/td[1]")).getText();
        String friendEmail2 = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[2]/td[1]")).getText();
        String friendEmail3 = driver.findElement(By.xpath("/html/body/div/table/tbody/tr[3]/td[1]")).getText();
        Assertions.assertEquals(friendEmail1, email);
        Assertions.assertEquals(friendEmail2, email2);
        Assertions.assertEquals(friendEmail3, email3);
    }
    // Prueba27: Mostrar el listado de amigos de un usuario. Comprobar que se incluye la información relacionada con la última publicación de cada usuario y la fecha de inicio de amistad.
    @Test
    @Order(27)
    public void PR27(){
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        String checkText = "Listado de usuarios";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);

        //Nos dirigimos a las solicitudes de amistad
        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendRequest");
        PO_HomeView.clickOptionById(driver, "friendRequest", "text","Nombre del Remitente");
        //Pulsamos en el botón aceptar

        WebElement acceptar = driver.findElement(By.xpath("//*[@id=\"friendRequestList\"]/tbody/tr[5]/td[4]/a"));
        acceptar.click();



        PO_HomeView.clickOptionById(driver, "friendDropDown","id","friendsList");
        PO_HomeView.clickOptionById(driver, "friendsList", "text","Nombre");
        List<WebElement> friendsList = SeleniumUtils.waitLoadElementsBy(driver, "class", "friends-list", 5);
        for (WebElement friend : friendsList) {
            // Verificar la información de correo electrónico
            String email = friend.findElement(By.className("friend-email")).getText();
            Assertions.assertNotNull(email, "Correo electrónico del amigo no encontrado");

            // Verificar el nombre
            String name = friend.findElement(By.className("friend-name")).getText();
            Assertions.assertNotNull(name, "Nombre del amigo no encontrado");

            // Verificar la fecha de inicio de amistad
            String friendshipDate = friend.findElement(By.className("friendship-date")).getText();
            Assertions.assertNotNull(friendshipDate, "Fecha de inicio de amistad no encontrada");

            // Verificar la última publicación
            String lastPost = friend.findElement(By.className("last-post")).getText();
           Assertions.assertNotEquals("No hay publicaciones",lastPost);

        }
    }
    @Test
    @Order(28)
    public void PR28(){
        driver.navigate().to("http://localhost:8081/listUsersSocialMedia");
        SeleniumUtils.textIsPresentOnPage(driver, "Identificación de usuario");
        Assertions.assertTrue(driver.findElement(By.name("email")).isDisplayed());
    }
    @Test
    @Order(29)
    public void PR29(){
        driver.navigate().to("http://localhost:8081/listUsersSocialMedia");
        SeleniumUtils.textIsPresentOnPage(driver, "Identificación de usuario");
        Assertions.assertTrue(driver.findElement(By.name("email")).isDisplayed());
        SeleniumUtils.textIsPresentOnPage(driver, "Debes estar autenticado para acceder a esta página.");
    }
    @Test
    @Order(30)
    public void PR30(){
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        driver.navigate().to("http://localhost:8081/listAllUsers");
        SeleniumUtils.textIsPresentOnPage(driver, "Identificación de usuario");
        Assertions.assertTrue(driver.findElement(By.name("email")).isDisplayed());
        SeleniumUtils.textIsPresentOnPage(driver, "No tienes permisos para acceder a esta página.");
    }
    @Test
    @Order(31)
    public void PR31(){
        // Navega a la página de registro
        PO_HomeView.clickRegister(driver);

        PO_SignUpView.fillForm(driver, "userprueba1", "vinn", "27/03/2000","user19@email.com", "Us3r@9-PASSW", "Us3r@9-PASSW");
        PO_HomeView.clickRegister(driver);
        PO_SignUpView.fillForm(driver, "userprueba2", "vinn", "27/03/2000","user18@email.com", "Us3r@9-PASSW", "Us3r@9-PASSW");
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user08@email.com", "Us3r@8-PASSW");
        PO_NavView.logout(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        PO_NavView.logout(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        WebElement logs = driver.findElement(By.id("logs"));
        logs.click();
        SeleniumUtils.textIsPresentOnPage(driver,"LOGIN");
        SeleniumUtils.textIsPresentOnPage(driver,"PET");
        SeleniumUtils.textIsPresentOnPage(driver,"ALTA");
        SeleniumUtils.textIsPresentOnPage(driver,"LOGOUT");
        SeleniumUtils.textIsPresentOnPage(driver,"/log/list");
        SeleniumUtils.textIsPresentOnPage(driver,"user09@email.com");
        SeleniumUtils.textIsPresentOnPage(driver,"user08@email.com");
    }
    @Test
    @Order(32)
    public void PR32(){
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "admin@email.com", "@Dm1n1str@D0r");
        WebElement logs = driver.findElement(By.id("logs"));
        logs.click();
        // Selecciona el tipo de log deseado
        WebElement selectElement = driver.findElement(By.name("logType"));
        Select select = new Select(selectElement);
        select.selectByVisibleText("LOGIN");


        WebElement deleteButton = driver.findElement(By.id("delete-logs-button"));
        deleteButton.click();

        List<WebElement> rows = driver.findElements(By.xpath("//table[@id='tableLogs']//tbody/tr"));
        Assertions.assertEquals(0, rows.size(), "La tabla de logs no está vacía después de la eliminación.");
    }
    @Test
    @Order(33)
    public void PR33(){
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        WebElement newPost = driver.findElement(By.id("createPost"));
        newPost .click();

        // Rellenar el formulario de creación de publicaciones
        WebElement title = driver.findElement(By.name("title"));
        title.click();
        title.clear();
        title.sendKeys("Mi primer post");

        WebElement content = driver.findElement(By.name("content"));
        content.click();
        content.clear();
        content.sendKeys("Este es el contenido de mi primer post.");

        // Pulsar el botón de envío
        WebElement submitButton = driver.findElement(By.id("submitPost"));
        submitButton.click();


        List<WebElement> rows = driver.findElements(By.xpath("//table[@id='tablePosts']//tbody/tr"));
        Assertions.assertEquals(5, rows.size());
        driver.findElement(By.linkText("2")).click();
        rows = driver.findElements(By.xpath("//table[@id='tablePosts']//tbody/tr"));
        Assertions.assertEquals(5, rows.size());
        driver.findElement(By.linkText("3")).click();
        rows = driver.findElements(By.xpath("//table[@id='tablePosts']//tbody/tr"));
        Assertions.assertEquals(1, rows.size());
    }
    @Test
    @Order(34)
    public void PR34(){
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        WebElement newPost = driver.findElement(By.id("createPost"));
        newPost .click();

        // Rellenar el formulario de creación de publicaciones
        WebElement title = driver.findElement(By.name("title"));
        title.click();
        title.clear();
        title.sendKeys("");

        WebElement content = driver.findElement(By.name("content"));
        content.click();
        content.clear();
        content.sendKeys("");

        // Pulsar el botón de envío
        WebElement submitButton = driver.findElement(By.id("submitPost"));
        submitButton.click();

        SeleniumUtils.textIsPresentOnPage(driver,"Todos lo campos son obligatorios.");
    }
    @Test
    @Order(35)
    public void PR35(){
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user09@email.com", "Us3r@9-PASSW");
        WebElement newPost = driver.findElement(By.id("myPosts"));
        newPost .click();

        List<WebElement> rows = driver.findElements(By.xpath("//table[@id='tablePosts']//tbody/tr"));
        Assertions.assertEquals(5, rows.size());
        driver.findElement(By.linkText("2")).click();
        Assertions.assertEquals(5, rows.size());
    }
    @Test
    @Order(36)
    public void PR36(){
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        WebElement myFriends = driver.findElement(By.id("friendList"));
        myFriends.click();
        driver.findElement(By.linkText("uo303984@uniovi.es")).click();
        SeleniumUtils.textIsPresentOnPage(driver,"Perfil de Eric Vivancos");
        List<WebElement> rows = driver.findElements(By.xpath("//table[@id='tablePosts']//tbody/tr"));
        Assertions.assertEquals(5, rows.size());
        driver.findElement(By.linkText("2")).click();
        rows = driver.findElements(By.xpath("//table[@id='tablePosts']//tbody/tr"));
        Assertions.assertEquals(5, rows.size());
    }
    @Test
    @Order(37)
    public void PR37(){
        // el usuario5 no tiene amistad con uo303984
        PO_LoginView.clickLogin(driver);
        PO_LoginView.fillLoginForm(driver, "user05@email.com", "Us3r@5-PASSW");
        WebElement myFriends = driver.findElement(By.id("friendList"));
        myFriends.click();
        SeleniumUtils.textIsNotPresentOnPage(driver,"uo303984@uniovi.es");
        driver.navigate().to("http://localhost:8081/profile/6635ee5923012671069168b1"); // id del usuario uo303984
        SeleniumUtils.textIsPresentOnPage(driver,"No tienes permiso para ver esta página.");

    }
    @Test
    @Order(38)
    public void PR38() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "admin@email.com");
        requestParams.put("password", "@Dm1n1str@D0r");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
        //verirficar la estrucutra del json retornada
        String json = response.getBody().asString();
        Assertions.assertTrue(json.contains("token"));

    }

    //inciar session correo correcto contra mal deberia dar 200
    @Test
    @Order(39)
    public void PR39() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "admin@email.com");
        requestParams.put("password", "malContraNoEsLaCorrecta.?");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha fallado y da un error
        Assertions.assertEquals(401, response.getStatusCode());
        //verirficar la estrucutra del json retornada
        String json = response.getBody().asString();
        Assertions.assertTrue(json.contains("error"));
    }
    //intentar inciar session con vacio error
    @Test
    @Order(40)
    public void PR40() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "");
        requestParams.put("password", "");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha fallado y da un error
        Assertions.assertEquals(401, response.getStatusCode());
        //verirficar la estrucutra del json retornada
        String json = response.getBody().asString();
        Assertions.assertTrue(json.contains("error"));
    }

    //mostrar el istado de amigos ordenado para un user autenticado
    @Test
    @Order(41)
    public void PR41(){
        Response response = makeRequestToUrl(loginApiURl,"uo303984@uniovi.es","Us3r@1-PASSW");
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
        //verirficar la estrucutra del json retornada
        String json = response.getBody().asString();
        Assertions.assertTrue(json.contains("token"));

        // Extraer el token del JSON
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        String token = jsonObject.getString("token");

        //pedir la lista de amigos usando el token
        Response getFriendsResponse = makeGetRequestWithToken(getFriendsApiURl, token);
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, getFriendsResponse.getStatusCode());
        //verirficar la estrucutra del json retornada
        String jsonFriends = getFriendsResponse.getBody().asString();
        Assertions.assertTrue(jsonFriends.contains("email"));
        Assertions.assertTrue(jsonFriends.contains("user02@email.com"));
        Assertions.assertTrue(jsonFriends.contains("user01@email.com"));
        Assertions.assertTrue(jsonFriends.contains("_id"));
        Assertions.assertTrue(jsonFriends.contains("name"));
        Assertions.assertTrue(jsonFriends.contains("surname"));
        // Convertir la cadena JSON en un JSONArray
        org.json.JSONObject jsonObject2 = new org.json.JSONObject(jsonFriends);
        JSONArray jsonArray =        jsonObject2.getJSONArray("friends");
        // Verificar que el JSONArray tiene exactamente 2 elementos
        Assertions.assertEquals(3, jsonArray.length());
    }

    //Prueba envío de mensajes a un amigo
    @Test
    @Order(42)
    public void PR42(){
        Response response = makeRequestToUrl(loginApiURl,"uo303984@uniovi.es","Us3r@1-PASSW");
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
        //verirficar la estrucutra del json retornada
        String json = response.getBody().asString();
        Assertions.assertTrue(json.contains("token"));

        // Extraer el token del JSON
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        String token = jsonObject.getString("token");

        //pedir la lista de amigos usando el token
        Response sendChatResponse = makePostRequestWithToken(sendChatMessageURL + "123456789012345678901234", token,
                "{\"message\": \"Hola\"}");
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, sendChatResponse.getStatusCode());

        //pedir la lista de amigos usando el token
        Response getFriendsResponse = makeGetRequestWithToken(getChatsApiURl, token);
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, getFriendsResponse.getStatusCode());
        //verirficar la estrucutra del json retornada
        String jsonChats = getFriendsResponse.getBody().asString();
        org.json.JSONObject jsonObject2 = new org.json.JSONObject(jsonChats);
        JSONArray jsonArray =jsonObject2.getJSONArray("sender");
        JSONArray jsonArrayReciver = jsonObject2.getJSONArray("reciver");


        // Verificar que el JSONArray tiene exactamente 2 elementos
        Assertions.assertEquals(3, jsonArray.length());
        Assertions.assertEquals(0, jsonArrayReciver.length());

    }

    //Prueba obtener los mensajes de una conversación
    @Test
    @Order(43)
    public void PR43(){
        Response response = makeRequestToUrl(loginApiURl,"uo303984@uniovi.es","Us3r@1-PASSW");
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
        //verirficar la estrucutra del json retornada
        String json = response.getBody().asString();
        Assertions.assertTrue(json.contains("token"));

        // Extraer el token del JSON
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        String token = jsonObject.getString("token");

        //pedir la lista de amigos usando el token
        Response getFriendsResponse = makeGetRequestWithToken(getMessagesApiUrl + "223456789012345678901234", token);
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, getFriendsResponse.getStatusCode());
        //verirficar la estrucutra del json retornada
        String jsonMessages = getFriendsResponse.getBody().asString();
        // Convertir la cadena JSON en un JSONArray
        JSONArray messages = new JSONArray(jsonMessages);
        Assertions.assertEquals(2, messages.length());

        System.out.println(jsonMessages);
        org.json.JSONObject message = messages.getJSONObject(0);
        Assertions.assertEquals("¡Hola, amigo!", message.getString("content"));
        org.json.JSONObject message2 = messages.getJSONObject(1);
        Assertions.assertEquals("¡Hola, qué tal!", message2.getString("content"));
        // Verificar que el JSONArray tiene exactamente 2 elementos
    }

    /**
     * obtener el listado de todas las conversaciones en las que participas
     */
    @Test
    @Order(44)
    public void PR44(){
        Response response = makeRequestToUrl(loginApiURl,"uo303984@uniovi.es","Us3r@1-PASSW");
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
        //verirficar la estrucutra del json retornada
        String json = response.getBody().asString();
        Assertions.assertTrue(json.contains("token"));

        // Extraer el token del JSON
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        String token = jsonObject.getString("token");

        //pedir la lista de amigos usando el token
        Response getFriendsResponse = makeGetRequestWithToken(getChatsApiURl, token);
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, getFriendsResponse.getStatusCode());
        //verirficar la estrucutra del json retornada
        String jsonFriends = getFriendsResponse.getBody().asString();
        Assertions.assertTrue(jsonFriends.contains("userId"));
        Assertions.assertTrue(jsonFriends.contains("friendId"));
        Assertions.assertTrue(jsonFriends.contains("messages"));
        // Convertir la cadena JSON en un JSONArray
        org.json.JSONObject jsonObject2 = new org.json.JSONObject(jsonFriends);
        JSONArray jsonArray =jsonObject2.getJSONArray("sender");
        JSONArray jsonArrayReciver =jsonObject2.getJSONArray("reciver");

        // Verificar que el JSONArray tiene exactamente 2 elementos
        Assertions.assertEquals(2, jsonArray.length());
        Assertions.assertEquals(0, jsonArrayReciver.length());

    }

    //Prueba eliminar una conversación
    @Test
    @Order(45)
    public void PR45(){
        Response response = makeRequestToUrl(loginApiURl,"uo303984@uniovi.es","Us3r@1-PASSW");
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());

        String json = response.getBody().asString();
        Assertions.assertTrue(json.contains("token"));

        // Extraer el token del JSON
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        String token = jsonObject.getString("token");

        Response deleteChatResponse = makeDeleteRequestWithToken(deleteChatApiUrl + "223456789012345678901234", token);
        Assertions.assertEquals(200, deleteChatResponse.getStatusCode());

        Response getFriendsResponse = makeGetRequestWithToken(getMessagesApiUrl + "223456789012345678901234", token);
        //Comprobamos que no esta el chat --> error
        Assertions.assertEquals(404, getFriendsResponse.getStatusCode());
    }

    //Prueba marcar un mensaje como leido
    @Test
    @Order(46)
    public void PR46(){
        Response response = makeRequestToUrl(loginApiURl,"uo303984@uniovi.es","Us3r@1-PASSW");
        //2. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());

        String json = response.getBody().asString();
        Assertions.assertTrue(json.contains("token"));

        // Extraer el token del JSON
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        String token = jsonObject.getString("token");

        Response readMessageResponse = makePutRequestWithToken(readMessageApiUrl + "223456789012345678901234/323456789012345678901234", token);
        Assertions.assertEquals(200, readMessageResponse.getStatusCode());

        Response getFriendsResponse = makeGetRequestWithToken(getMessagesApiUrl + "223456789012345678901234", token);
        //Comprobamos que no esta el chat --> error
        Assertions.assertEquals(200, getFriendsResponse.getStatusCode());

        String jsonMessages = getFriendsResponse.getBody().asString();
        // Convertir la cadena JSON en un JSONArray
        JSONArray messages = new JSONArray(jsonMessages);
        Assertions.assertEquals(2, messages.length());

        System.out.println(jsonMessages);
        org.json.JSONObject message = messages.getJSONObject(0);
        Assertions.assertTrue(message.getBoolean("read"));
        org.json.JSONObject message2 = messages.getJSONObject(1);
        Assertions.assertFalse(message2.getBoolean("read"));
    }

    //Probamos a iniciar sesión con datos válidos y comporbamos que dirige al usuario a la lista de amigos
    @Test
    @Order(47)
    public void PR47() {
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
        String checkText = "Mis Amigos";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }

    //Probamos a iniciar sesión con datos inválidos y comporbamos que se muestra un mensaje
    @Test
    @Order(48)
    public void PR48() {
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "email@erroneo.es", "ninguna");
        String checkText = "Usuario o contraseña incorrectos.";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }

    //Probamos a iniciar sesión con los campos vacíos y comporbamos que se muestra un mensaje
    @Test
    @Order(49)
    public void PR49() {
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "", "");
        String checkText = "Rellene los campos vacíos.";
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        PO_LoginView.fillLoginForm(driver, "", "gbfb");
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
        PO_LoginView.fillLoginForm(driver, "effre", "");
        SeleniumUtils.textIsPresentOnPage(driver, checkText);
    }

    //Probamos a iniciar sesión y comprobamos que el usuario lista sus amigos
    @Test
    @Order(50)
    public void PR50() {
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
        int rows = driver.findElements(By.xpath("//*[@id=\"friendsTableBody\"]/tbody/tr")).size();
        int columns = driver.findElements(By.xpath("//*[@id=\"friendsTableBody\"]/tbody/tr/td")).size();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String elementXpath = String.format("//*[@id='friendsTableBody']//tbody/tr[%d]/td[%d]", i, j);
                WebElement element = driver.findElement(By.xpath(elementXpath));
                // Asegurarse de que la celda de fecha y del nombre no estén vacías
                Assertions.assertFalse(element.getText().isEmpty(), "La celda no debe estar vacía");
            }
        }
    }
    @Test
    @Order(51)
    public void PR51(){
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
        driver.findElement(By.id("chatButton-user02@email.com")).click();
        WebElement input =driver.findElement(By.xpath("//input"));
        input.click();
        input.sendKeys("Hola, estoy probando el funcionamiento");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        SeleniumUtils.textIsPresentOnPage(driver,"Hola, estoy probando el funcionamiento");

    }
    @Test
    @Order(52)
    public void PR52(){
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
        driver.findElement(By.linkText("Mis chats")).click();
        WebElement chatReanudarButon = driver.findElement(By.xpath("//tbody/tr[1]/td[last()]/button[2]"));
        chatReanudarButon.click();
        SeleniumUtils.textIsPresentOnPage(driver,"¡Hola, amigo!");
        WebElement input =driver.findElement(By.xpath("//input"));
        input.click();
        input.sendKeys("Hola, estoy probando el funcionamiento");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        SeleniumUtils.textIsPresentOnPage(driver,"Hola, estoy probando el funcionamiento");
    }
    @Test
    @Order(53)
    public void PR53(){
            driver.navigate().to(URL_CLIENTE);
            PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
            int rows = driver.findElements(By.xpath("//tbody/tr")).size();

            String elementXpath = String.format("//tbody/tr[%d]/td[last()]/button", 2);
            driver.findElement(By.id("chatButton-user02@email.com")).click();
            WebElement input = driver.findElement(By.xpath("//input"));
            input.click();
            input.sendKeys("Estoy probando el funcionamiento");
            driver.findElement(By.xpath("//button[@type='submit']")).click();
            SeleniumUtils.textIsPresentOnPage(driver,"Estoy probando el funcionamiento");
    }
    @Test
    @Order(54)
    public void PR54(){
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
        driver.findElement(By.linkText("Mis chats")).click();
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertEquals(2,rows.size());
        WebElement chatReanudarButon = driver.findElement(By.xpath("//tbody/tr[1]/td[last()]/button[2]"));
        chatReanudarButon.click();
        SeleniumUtils.textIsPresentOnPage(driver,"¡Hola, amigo!");

    }
    @Test
    @Order(55)
    public void PR55(){
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
        driver.findElement(By.linkText("Mis chats")).click();
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertEquals(2,rows.size());
        WebElement chatReanudarButon = driver.findElement(By.xpath("//tbody/tr[1]/td[last()]/button[1]"));
        chatReanudarButon.click();
        SeleniumUtils.textIsPresentOnPage(driver,"Se ha eliminado el chat correctamente.");
        rows = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertEquals(1,rows.size());
    }
    @Test
    @Order(56)
    public void PR56(){
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
        driver.findElement(By.linkText("Mis chats")).click();
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertEquals(2,rows.size());
        WebElement chatReanudarButon = driver.findElement(By.xpath("//tbody/tr[last()]/td[last()]/button[1]"));
        chatReanudarButon.click();
        SeleniumUtils.textIsPresentOnPage(driver,"Se ha eliminado el chat correctamente.");

    }
    //Prueba57: Identificarse en la aplicación, enviar tres mensajes a un amigo y validar que los mensajes enviados aparecen en el chat.
    @Test
    @Order(57)
    public void PR57() {
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
        driver.findElement(By.linkText("Mis chats")).click();
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertEquals(2, rows.size());
        WebElement chatReanudarButon = driver.findElement(By.xpath("//tbody/tr[1]/td[last()]/button[2]"));
        chatReanudarButon.click();

        //una vez reanudada la conver mandar 3 mensajes
        WebElement input = driver.findElement(By.xpath("//input"));
        for(int i=0;i<3;i++){
            input.click();
            input.sendKeys("Mensaje de prueba "+i);
            driver.findElement(By.xpath("//button[@type='submit']")).click();
        }
        SeleniumUtils.textIsPresentOnPage(driver,"Mensaje de prueba 0");
        SeleniumUtils.textIsPresentOnPage(driver,"Mensaje de prueba 1");
        SeleniumUtils.textIsPresentOnPage(driver,"Mensaje de prueba 2");

        //logearse con el otro usuario el user01
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        driver.findElement(By.linkText("Amigos")).click();
        List<WebElement> rows2 = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertEquals(1, rows2.size());
        //comprobar que el numero de mensajes es de 4 en este caso porque siempre tiene 1 sin leer
        //el xpath del cmapo es este /html/body/div/div/table/tbody/tr/td[6]
        WebElement chatReanudarButon2 = driver.findElement(By.xpath("/html/body/div/div/table/tbody/tr/td[6]"));
        //obtener el texto de ese elemento y comprobar que sea 4
        Assertions.assertEquals("4",chatReanudarButon2.getText());

        //EXTRA PORQUE CREO QUE EL TEST MAL SERIA COMPROBAR QUE AL MANDAR UN MSG CON 2 NAVEGADORES ABIERTOS LOS Q TU MANDAS SE PONEN EN LEIDO
        //Y LOS QUE TE MANDAN SE PONEN EN NO LEIDO


    }

    /**
     * como el test de la historia no parece completo s ev a ahcer este test lanzado  2
     * navegadores en apralelo y coprobando que al usuario se le pongan en leido los
     * mensajes
     *
     */
    @Test
    @Order(59)
    public void PR57_CUSTOM(){
        WebDriver driver2 = new FirefoxDriver();


        //ir al chat como normalmente
        driver2.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver2, "uo303984@uniovi.es", "Us3r@1-PASSW");
        driver2.findElement(By.linkText("Mis chats")).click();
        List<WebElement> rows3 = driver2.findElements(By.xpath("//tbody/tr"));
        Assertions.assertEquals(2, rows3.size());
        WebElement chatReanudarButon3 = driver2.findElement(By.xpath("//tbody/tr[1]/td[last()]/button[2]"));
        chatReanudarButon3.click();

        //unir al otro user al chat
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        driver.findElement(By.linkText("Amigos")).click();
        WebElement conversar= driver.findElement(By.id("chatButton-uo303984@uniovi.es"));
        conversar.click();


        //ahora tu mandaras dos 3 mensajes y comprobaras que se van poniendo en leido
        WebElement input2 = driver2.findElement(By.xpath("//input"));
        for(int i=0;i<4;i++){
            input2.click();
            input2.sendKeys("Mensaje de prueba "+i);
            driver2.findElement(By.xpath("//button[@type='submit']")).click();
        }
        //no hace falta comprobar los 3 si se mando uno el resto tb
        SeleniumUtils.textIsPresentOnPage(driver2,"Mensaje de prueba 0");
        SeleniumUtils.textIsPresentOnPage(driver,"Mensaje de prueba 0");

        //comprobar que en el chat del que escribio los mensajes ahora esta presente el leido
        //los mensajes esta en unn chat messalist lo obtienes y sacas los ultimos 3 elementos
        //y compruebas que el ultimo es leido
        // Encuentra todos los divs dentro del div con id "chatMessageList"
        List<WebElement> allDivs = driver2.findElements(By.xpath("//*[@id='chatMessageList']/div"));

        // Comienza desde el penúltimo elemento y recorre tres elementos
        for (int i = allDivs.size() - 2; i > allDivs.size() - 5; i--){
            WebElement div = allDivs.get(i);

            // Comprueba que en cada div de esos está presente el texto "Leído"
            Assertions.assertTrue(div.getText().contains("Leído"));
        }

    }
    //
    @Test
    @Order(58)
    public void PR58() {

        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "uo303984@uniovi.es", "Us3r@1-PASSW");
        driver.findElement(By.linkText("Mis chats")).click();
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertEquals(2, rows.size());
        WebElement chatReanudarButon = driver.findElement(By.xpath("//tbody/tr[1]/td[last()]/button[2]"));
        chatReanudarButon.click();

        //una vez reanudada la conver mandar 3 mensajes
        WebElement input = driver.findElement(By.xpath("//input"));
        for(int i=0;i<3;i++){
            input.click();
            input.sendKeys("Mensaje de prueba "+i);
            driver.findElement(By.xpath("//button[@type='submit']")).click();
        }
        SeleniumUtils.textIsPresentOnPage(driver,"Mensaje de prueba 0");
        SeleniumUtils.textIsPresentOnPage(driver,"Mensaje de prueba 1");
        SeleniumUtils.textIsPresentOnPage(driver,"Mensaje de prueba 2");

        //logearse con el otro usuario el user01
        driver.navigate().to(URL_CLIENTE);
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "Us3r@1-PASSW");
        driver.findElement(By.linkText("Amigos")).click();
        List<WebElement> rows2 = driver.findElements(By.xpath("//tbody/tr"));
        Assertions.assertEquals(1, rows2.size());
        //comprobar que el numero de mensajes es de 4 en este caso porque siempre tiene 1 sin leer
        //el xpath del cmapo es este /html/body/div/div/table/tbody/tr/td[6]
        WebElement chatReanudarButon2 = driver.findElement(By.xpath("/html/body/div/div/table/tbody/tr/td[6]"));
        //obtener el texto de ese elemento y comprobar que sea 4
        Assertions.assertEquals("4",chatReanudarButon2.getText());
    }

    public Response makeRequestToUrl(String url, String email, String password) {
        final String RestAssuredURL = url;
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", email);
        requestParams.put("password", password);
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        return request.post(RestAssuredURL);
    }

    public Response makeGetRequestWithToken(String url, String token) {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("token", token);
        return request.get(url);
    }

    public Response makePostRequestWithToken(String url, String token, String body) {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("token", token);
        request.body(body);
        return request.post(url);
    }
    public Response makeDeleteRequestWithToken(String url, String token) {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("token", token);
        return request.delete(url);
    }

    public Response makePutRequestWithToken(String url, String token) {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("token", token);
        return request.put(url);
    }
}
