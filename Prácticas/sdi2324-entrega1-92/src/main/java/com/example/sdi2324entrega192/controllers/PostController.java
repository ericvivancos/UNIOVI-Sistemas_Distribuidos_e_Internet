package com.example.sdi2324entrega192.controllers;

import com.example.sdi2324entrega192.FileUploadUtil;
import com.example.sdi2324entrega192.entities.Post;
import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.services.*;
import com.example.sdi2324entrega192.validators.PostEditFormValidator;
import com.example.sdi2324entrega192.validators.PostFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.LinkedList;import java.util.UUID;

/**
 * Controlador para gestionar las operaciones relacionadas con los posts.
 */
@Controller
public class PostController {
    @Autowired
    private LogService logService;
    Logger logger = LoggerFactory.getLogger(FriendsController.class);
    //obtener el direcotiro donde etsan las img
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final PostService postService;
    private final UsersService usersService;
    private final PostFormValidator postFormValidator;
    private final FriendRequestService friendRequestService;
    private final FriendshipService friendshipService;
    private final PostEditFormValidator postEditFormValidator;
    private final PostStatusService postStatusService;
    private final RecommendationService recommendationService;

    /**
     * Constructor de la clase PostController.
     *
     * @param postService           Servicio para gestionar los posts.
     * @param usersService          Servicio para gestionar los usuarios.
     * @param postFormValidator     Validador para el formulario de post.
     * @param postEditFormValidator Validador para el formulario de edición de post.
     * @param postStatusService     Servicio para gestionar los estados de post.
     * @param friendRequestService  Servicio para gestionar las solicitudes de amistad.
     * @param friendshipService     Servicio para gestionar las amistades.
     * @param recommendationService Servicio para gestionar las recomendaciones de post.
     */
    public PostController(PostService postService,  UsersService usersService,  PostFormValidator postFormValidator, PostEditFormValidator postEditFormValidator, PostStatusService postStatusService, FriendRequestService friendRequestService, FriendshipService friendshipService, RecommendationService recommendationService){
        this.postService = postService;
        this.usersService = usersService;
        this.postFormValidator = postFormValidator;
        this.postEditFormValidator = postEditFormValidator;
        this.postStatusService = postStatusService;
        this.friendRequestService = friendRequestService;
        this.friendshipService = friendshipService;
        this.recommendationService = recommendationService;

    }

    /**
     * Método para obtener la vista de agregar un nuevo post.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @return El nombre de la vista para agregar un nuevo post.
     */
    @RequestMapping(value="post/add")
    public String getPost(Model model){
        model.addAttribute("post",new Post());
        logger.info(
                logService.logRequest("PostController --> post/add" , "GET", new String[]{})
        );
        return "post/add";
    }

    /**
     * Método para agregar un nuevo post.
     *
     * @param principal  La información de autenticación del usuario.
     * @param post       El post que se desea agregar.
     * @param imageFile  El archivo de imagen asociado al post.
     * @param result     El resultado del proceso de validación.
     * @return La URL a la que se redireccionará el usuario.
     */
    @RequestMapping(value="post/add", method = RequestMethod.POST)
    public String addPost(Principal principal, @Validated Post post, @RequestParam("imageFile") MultipartFile imageFile, BindingResult result){
        postFormValidator.validate(post,result);
        User user=usersService.getUserByEmail(principal.getName());

        if(result.hasErrors()){
            return "post/add";
        }

        // Maneja la carga de la imagen
        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String userDir = "uploads/" + user.getId();

            try {
                String url = FileUploadUtil.saveFile(userDir, imageFile);
                // Establece la URL de la imagen en el objeto Post ojo aqui es absoluta no relativa
                post.setPhotoUrl("/"+userDir+ "/" + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Post newPost = new Post(post.getTitle(),LocalDate.now(),post.getContent());
        newPost.setOwner(usersService.getUserByEmail(principal.getName()));
        newPost.setPhotoUrl(post.getPhotoUrl());
        postService.addNewPost(newPost);
        logger.info(
                logService.logRequest("PostController --> post/add" , "POST", new String[]{post.getTitle(),post.getContent()})
        );
        return "redirect:/home";
    }


    @RequestMapping("/post/list")
    public String getList(Model model, Pageable pageable, Principal principal, HttpSession session){
        String mail = principal.getName(); // DNI es el name de la autenticación
        User user = usersService.getUserByEmail(mail);
        //eliminar en caso de que tuviera el amigo de la sesion , no deberia tenerlo x el interceptor pero por si acaso
        session.removeAttribute("userToSearch");

        Page<Post> posts = conservarBusqueda(session,"user",pageable,user);


        model.addAttribute("postsList", posts.getContent());
        model.addAttribute("user", user);
        model.addAttribute("page", posts);
        // Almacena el tipo de búsqueda en la sesión para que la busqueda funcione
        session.setAttribute("searchingFor", "user");
        // Agregar un atributo al modelo para indicar que no se está mostrando la vista de amigos
        model.addAttribute("friendsView", false);
        logger.info(
                logService.logRequest("PostController --> post/list" , "GET", new String[]{})
        );
        return "post/list";
    }

    /**
     * Método para obtener la lista de posts de amigos de un usuario.
     *
     * @param model      El modelo que se utilizará para pasar datos a la vista.
     * @param pageable   La información de paginación para la lista de posts.
     * @param id         El ID del usuario cuyos posts de amigos se quieren obtener.
     * @param session    La sesión actual.
     * @param principal  El usuario autenticado.
     * @return El nombre de la vista para la lista de posts de amigos, o "accessDenied" si no se tienen permisos.
     */
    @RequestMapping("/post/friendList/{id}")
    public String getListFriends(Model model, Pageable pageable, @PathVariable Long id,HttpSession session,Principal principal) {
        User currentUser = usersService.getUserByEmail(principal.getName());
        User user = usersService.getUser(id);
        if(friendRequestService.areAlreadyFriends(currentUser, user) || friendshipService.areAlreadyFriends(currentUser, user)) {

            //eliminar en caso de que tuviera el amigo de la sesion , no deberia tenerlo x el interceptor pero por si acaso
            session.removeAttribute("userToSearch");

            Page<Post> posts = conservarBusqueda(session,"friends",pageable,user);
            model.addAttribute("postsList", posts.getContent());
            model.addAttribute("user", user);
            model.addAttribute("page", posts);

            // Agregar un atributo al modelo para indicar que se está mostrando la vista de amigos
            model.addAttribute("friendsView", true);
            // Obtener las publicaciones recomendadas para el usuario actual y agregarlas al modelo
            model.addAttribute("recommendedPosts", recommendationService.getRecommendedPosts(pageable, currentUser).getContent());

            // Almacena el tipo de búsqueda en la sesión para que la busqueda funcione
            session.setAttribute("searchingFor", "friends");
            //guardar el usuarsio en sesion por el cual buscaras
            session.setAttribute("userToSearch", user.getEmail());
            logger.info(
                    logService.logRequest("PostController --> post/friendList/"+id.toString() , "GET", new String[]{id.toString()})
            );
            return "post/list";
        }else{

            return "accessDenied";
        }

    }

    /**
     * Método para obtener la lista completa de posts.
     *
     * @param model     El modelo que se utilizará para pasar datos a la vista.
     * @param pageable  La información de paginación para la lista de posts.
     * @param session   La sesión actual.
     * @return El nombre de la vista para la lista completa de posts.
     */
    @RequestMapping(value = "/post/listAll", method = RequestMethod.GET)
    public String getList(Model model, Pageable pageable, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User activeUser = usersService.getUserByEmail(email);



        Page<Post> posts;

        //obtener los post correspiendtes , si estas buscando o sino los post completos
        posts = conservarBusqueda(session, "all", pageable, activeUser);


        model.addAttribute("postsList", posts.getContent());
        model.addAttribute("page", posts);

        // Almacena el tipo de búsqueda en la sesión para que la busqueda funcione
        session.setAttribute("searchingFor", "all");
        logger.info(
                logService.logRequest("PostController --> post/listAll" , "GET", new String[]{})
        );
        return "post/list";
    }

    /**
     * Método para obtener la vista de edición de un post.
     * Solo los usuarios con el rol de administrador pueden acceder a esta función.
     *
     * @param model El modelo que se utilizará para pasar datos a la vista.
     * @param id    El ID del post que se va a editar.
     * @return El nombre de la vista para la edición del post, o una redirección a la página de inicio de sesión si el usuario no tiene permiso.
     */
    @RequestMapping(value = "/post/edit/{id}")
    public String getEdit(Model model, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User activeUser = usersService.getUserByEmail(email);

        // Verificar si el usuario tiene el rol de administrador
        if (!activeUser.getRole().equals("ROLE_ADMIN")) { // Asume que tienes un método para verificar el rol
            // Redirigir al usuario a la página de inicio de sesión
            return "redirect:/login"; // Cambia "/login" por la ruta correcta de tu página de inicio de sesión
        }

        model.addAttribute("post", postService.getPostById(id));
        //pasarle la lista de estados de la publicacion
        model.addAttribute("postStatus", postStatusService.getStatus());
        logger.info(
                logService.logRequest("PostController --> post/edit/"+id.toString() , "GET", new String[]{id.toString()})
        );
        return "post/edit";
    }

    /**
     * Método para aplicar los cambios realizados en la edición de un post.
     * Este método se invoca cuando se envía el formulario de edición de un post.
     *
     * @param post   El post con los cambios realizados.
     * @param id     El ID del post que se está editando.
     * @param model  El modelo que se utilizará para pasar datos a la vista.
     * @param result El objeto BindingResult que contiene los resultados de la validación del formulario.
     * @return Una cadena que representa la URL a la que se redireccionará el usuario después de la edición del post, o la vista de edición del post con los errores si la validación falla.
     */
    @RequestMapping(value="/post/edit/{id}", method=RequestMethod.POST)
    public String setEdit(@Validated Post post,@PathVariable Long id, Model model, BindingResult result){
        postEditFormValidator.validate(post,result);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (result.hasErrors()) {
            //le pasas el mark a la vista para que saque los errores
            model.addAttribute("post", postService.getPostById(id));
            model.addAttribute("postStatus", postStatusService.getStatus());
            //agregar los errores
            model.addAttribute("Error","No se puede poner el mismo estatus que antes");



            return "post/edit";
        }

        Post originalPost = postService.getPostById(id);
        // modificar solo score y description
        originalPost.setStatus(post.getStatus());
        postService.addNewPost(originalPost);
        logger.info(
                logService.logRequest("PostController --> post/edit/"+id.toString() , "POST", new String[]{id.toString(),post.getTitle(),post.getContent(),post.getStatus()})
        );
        return "redirect:/post/listAll";
    }

    /**
     * Método para realizar una búsqueda de posts según un texto de búsqueda.
     * Este método se invoca cuando se realiza una búsqueda de posts.
     *
     * @param searchTextPost El texto a buscar en los posts.
     * @param model          El modelo que se utilizará para pasar datos a la vista.
     * @param pageable       El objeto Pageable que permite la paginación de los resultados.
     * @param session        La sesión HTTP para almacenar y recuperar datos de la sesión del usuario.
     * @return Una cadena que representa la vista que se mostrará al usuario después de realizar la búsqueda de posts.
     */
    @RequestMapping(value = "/post/search")
    public String search(@RequestParam(name = "searchTextPost", required = false) String searchTextPost,
                         Model model, Pageable pageable, HttpSession session){
        // Obtén el usuario y su rol desde la autenticación
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User userToSearch = usersService.getUserByEmail(email);

        //si estas buscando un amigo o tus posts
        if(session.getAttribute("userToSearch")!=null){
            String emailFriend= (String) session.getAttribute("userToSearch");
            userToSearch=usersService.getUserByEmail(emailFriend);
        }

        // Recupera que tipo de datos quieres ver si all , friendpost  o userpost
        String searchingFor = (String) session.getAttribute("searchingFor");


        Page<Post> searchResults=null;

        //compobar si ya has buscado par no perder la barra de busqueda en paginaciones
        if (searchTextPost != null && !searchTextPost.isBlank()) {
            session.setAttribute("searchTextPost", searchTextPost); //Guardamos en sesión la búsqueda
            searchResults=postService.getPostBySearchViewWithFilter(searchTextPost, pageable,userToSearch,searchingFor);
        } else {
            //Si ha realizado una búsqueda de texto vacío eliminamos de sesión el atributo
            if (searchTextPost != null) {
                session.removeAttribute("searchTextPost");
            }

            searchResults=postService.getPostBySearchView(pageable,userToSearch,searchingFor);
        }
        // Agrega los resultados de la búsqueda al modelo
        model.addAttribute("postsList", searchResults.getContent());
        model.addAttribute("page", searchResults);
        model.addAttribute("searchTextPost", searchTextPost);

        // También puedes agregar otros datos necesarios para la vista

        logger.info(
                logService.logRequest("PostController --> /post/search" ,"GET", new String[]{searchTextPost})
        );
        return "fragments/postListTable";  // Devuelve el fragmento actualizado
    }

    /**
     * Método privado para conservar los criterios de búsqueda durante la paginación de resultados de búsqueda de posts.
     * Este método se utiliza para mantener los criterios de búsqueda de posts en la sesión del usuario mientras se navega
     * entre las páginas de resultados de búsqueda.
     *
     * @param session   La sesión HTTP para almacenar y recuperar datos de la sesión del usuario.
     * @param searchfor El tipo de búsqueda que se está realizando: "all" para todos los posts, "friends" para los posts de amigos, etc.
     * @param pageable  El objeto Pageable que permite la paginación de los resultados.
     * @param activeUser El usuario activo que está realizando la búsqueda.
     * @return Un objeto Page que contiene los resultados de la búsqueda de posts según los criterios conservados.
     */
    private Page<Post> conservarBusqueda(HttpSession session ,String searchfor,Pageable pageable,User activeUser){
        //scar edl criterio de busqueda
        //compobar si ya has buscado par no perder la barra de busqueda en paginaciones
        String searchTextPost = (String) session.getAttribute("searchTextPost");
        Page<Post> posts;

        if (searchTextPost != null && !searchTextPost.isBlank()) {
            session.setAttribute("searchTextPost", searchTextPost); //Guardamos en sesión la búsqueda
            posts=postService.getPostBySearchViewWithFilter(searchTextPost, pageable,activeUser,searchfor);
        } else {
            //Si ha realizado una búsqueda de texto vacío eliminamos de sesión el atributo
            if (searchTextPost != null) {
                session.removeAttribute("searchTextPost");
            }

            posts=postService.getPostBySearchView(pageable,activeUser,searchfor);
        }



        return posts;
    }


}
