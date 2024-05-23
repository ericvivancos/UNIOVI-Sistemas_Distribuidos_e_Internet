package com.example.sdi2324entrega192.controllers;

import com.example.sdi2324entrega192.entities.*;
import com.example.sdi2324entrega192.services.*;

import com.example.sdi2324entrega192.validators.EditFormValidator;
import com.example.sdi2324entrega192.validators.SignUpFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing user-related operations.
 */
@Controller
public class UsersController {
    private final SignUpFormValidator signUpFormValidator;
    private final UsersService usersService;
    private final SecurityService securityService;
    private final RolesService rolesService;
    private final EditFormValidator editFormValidator;
    private final HttpSession httpSession;
    private final LogService logService;
    private final FriendRequestService friendRequestService;
    @Autowired
    private MessageSource messageSource;
    Logger logger = LoggerFactory.getLogger(UsersController.class);

    /**
     * Constructor for UsersController.
     *
     * @param usersService         Service for managing user-related operations.
     * @param securityService      Service for security-related operations.
     * @param signUpFormValidator Validator for sign up form.
     * @param rolesService        Service for managing roles.
     * @param editFormValidator   Validator for edit form.
     * @param httpSession         HTTP session.
     * @param logService          Service for logging.
     * @param friendRequestService Service for managing friend requests.
     */
    public UsersController(UsersService usersService, SecurityService securityService, SignUpFormValidator signUpFormValidator, RolesService rolesService, EditFormValidator editFormValidator, HttpSession httpSession,LogService logService,FriendRequestService friendRequestService){
        this.securityService = securityService;
        this.usersService = usersService;
        this.signUpFormValidator=signUpFormValidator;
        this.rolesService = rolesService;
        this.editFormValidator = editFormValidator;
        this.httpSession = httpSession;
        this.logService = logService;
        this.friendRequestService = friendRequestService;
    }

    /**
     * Obtiene una lista paginada de usuarios.
     *
     * @param model      Atributos del modelo para la vista.
     * @param pageable   Información de paginación.
     * @param principal  Representa al usuario autenticado actualmente.
     * @param searchText Texto de búsqueda para filtrar usuarios.
     * @return El nombre de la vista para mostrar la lista de usuarios.
     */
    @RequestMapping("/user/list")
    public String getUsers(Model model, Pageable pageable, Principal principal, @RequestParam(value = "", required = false)String searchText){
        String email = principal.getName();
        User user = usersService.getUserByEmail(email);
        Page<User> users;

        //Comprobamos si el usuario ha realizado una búsqueda
        if (searchText != null && !searchText.isBlank()) {
            httpSession.setAttribute("searchText", searchText); //Guardamos en sesión la búsqueda
            users = usersService.getUsersByNameLastnameAndEmail(pageable, searchText, user); //Filtramos
        } else {
            //Si ha realizado una búsqueda de texto vacío eliminamos de sesión el atributo
            if (searchText != null) {
                httpSession.removeAttribute("searchText");
            }
            Object sessionSearchText = httpSession.getAttribute("searchText");
            //Buscamos por searchText
            if (sessionSearchText != null) {
                users = usersService.getUsersByNameLastnameAndEmail(pageable, sessionSearchText.toString(), user);
            } else {
                //Mostramos los usuarios
                users = usersService.getUsers(pageable, user);
            }
        }
        List<User> usersList = users.getContent();
        List<Boolean> areFriendsList = usersList.stream().map(u -> friendRequestService
                .areAlreadyFriends(user,u))
                .collect(Collectors.toList());
        model.addAttribute("usersList", users.getContent());
        model.addAttribute("areFriendsList", areFriendsList);
        model.addAttribute("page", users);
        logger.info(
                logService.logRequest("UserController --> /user/list","GET",new String[]{})
        );
        return "user/list";
    }
    /**
     * Obtiene la vista para ver los detalles de un usuario.
     *
     * @param model Modelo para la vista.
     * @param id    ID del usuario del que se mostrarán los detalles.
     * @return El nombre de la vista para ver los detalles del usuario.
     */
    @RequestMapping("/user/details/{id}")
    public String getDetail(Model model, @PathVariable Long id){
        model.addAttribute("user",usersService.getUser(id));
        return "user/details";
    }

    /**
     * Obtiene la vista para editar un usuario existente.
     *
     * @param model Modelo para la vista.
     * @param id    ID del usuario a editar.
     * @return El nombre de la vista para editar un usuario.
     */
    @RequestMapping(value="/user/edit/{id}")
    public String getEdit(Model model,@PathVariable Long id){
        User user = usersService.getUser(id);
        model.addAttribute("rolesList", rolesService.getRoles());
        model.addAttribute("user",user);
        return "user/edit";
    }
    /**
     * Procesa la solicitud para editar un usuario existente.
     *
     * @param user  Usuario a editar.
     * @param id    ID del usuario a editar.
     * @param res   Resultado del proceso de validación.
     * @param model Modelo para la vista.
     * @return Redirecciona a la vista de detalles del usuario después de la edición si no hay errores, de lo contrario, vuelve a la vista de edición.
     */
    @RequestMapping(value = "/user/edit/{id}",method = RequestMethod.POST)
    public String setEdit(@Validated User user, @PathVariable Long id, BindingResult res, Model model){
        editFormValidator.validate(user, res);
        if(res.hasErrors()){
            model.addAttribute("rolesList", rolesService.getRoles());
            return "/user/edit";
        }
        usersService.updateUser(user);
        logger.info(
                logService.logRequest("UsersController --> /user/edit/"+id+"/","POST",new String[]{id.toString()})
        );
        return "redirect:/user/details/"+id;
    }
    /**
     * Obtiene la vista para el formulario de registro de nuevos usuarios.
     *
     * @param model Modelo para la vista.
     * @return El nombre de la vista para el formulario de registro.
     */
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        logger.info(
                logService.logRequest("UserController --> /signup","GET",new String[] {})
        );
        User user = new User();
        user.setRole("ROLE_USER");
        model.addAttribute("user", user);
        return "signup";
    }
    /**
     * Obtiene la vista para el formulario de inicio de sesión.
     *
     * @param request Solicitud HTTP.
     * @param model   Modelo para la vista.
     * @return El nombre de la vista para el formulario de inicio de sesión.
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        // Verificar si hay un error almacenado en la sesión
        Object authException = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        // Agregar el error y su mensaje
        if (authException != null) {
            // Manejar el error y limpiar la sesión
            model.addAttribute("error", authException.toString());
            request.getSession().removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
        // Verificar si hay un mensaje de error en la sesión
        Object errorMessage = request.getSession().getAttribute("errors");
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage.toString());
            request.getSession().removeAttribute("error");
        }

        logger.info(
                logService.logRequest("UserController --> /login", "GET", new String[]{})
        );
       return "login";
    }
    @RequestMapping(value = "/login/error", method = RequestMethod.GET)
    public String loginError(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("logoutMessage", true);
        return "redirect:/login";
    }

    /**
     * Maneja la solicitud POST para registrar un nuevo usuario.
     *
     * @param user El usuario a registrar.
     * @param result El resultado del proceso de validación.
     * @return La vista correspondiente después de procesar la solicitud.
     */
    @RequestMapping(value = "/signup",method = RequestMethod.POST)
    public String signup(@Validated User user, BindingResult result) {
        String[] params = new String[] {"username="+user.getEmail(),
                "password="+user.getPassword(),
                "name="+user.getName(),
                "lastname="+user.getLastName()
        };
        logger.info(
                logService.logRequest("UserController --> /signup","POST",params)
        );
        signUpFormValidator.validate(user,result);
        if(result.hasErrors()){
            return "signup";
        }
        user.setRole(rolesService.getRoles()[0]);
        usersService.addUser(user);

        logger.info(
                logService.logUserCreation("UserController","POST",params)
        );
        securityService.autoLogin(user.getEmail(),user.getPasswordConfirm());
        return "redirect:home";
    }



    /**
     * Obtiene la vista de inicio.
     *
     * @param model    Modelo para la vista.
     * @param pageable Objeto Pageable para la paginación.
     * @return El nombre de la vista de inicio.
     */
    @RequestMapping(value = {"/home"}, method = RequestMethod.GET)
    public String home(Model model, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User activeUser = usersService.getUserByEmail(email);

        model.addAttribute("usersList", usersService.getUsers(pageable, activeUser));
        logger.info(
                logService.logRequest("UserController --> /home", "GET", new String[]{})
        );
        return "home";
    }

    /**
     * Procesa la solicitud para eliminar usuarios.
     *
     * @param model    Modelo para la vista.
     * @param userIds  Lista de IDs de usuarios a eliminar.
     * @param pageable Objeto Pageable para la paginación.
     * @return El fragmento de la tabla de usuarios actualizado después de la eliminación.
     */
    @RequestMapping(value = "/user/list/delete")
    public String deleteUsers(Model model,
                              @RequestParam(value = "userIds", required = false) List<Long> userIds,
                              Pageable pageable) {
        Long userId = usersService.getAuthenticatedUser().getId();
        if (userIds == null || userIds.isEmpty()) {
            String errorMessage = messageSource.getMessage("Error.noSelectedUser", null, LocaleContextHolder.getLocale());
            model.addAttribute("ErrorMessage", errorMessage);
        } else if (userIds.contains(userId)) {
            String errorMessage = messageSource.getMessage("Error.noDeleteOwner", null, LocaleContextHolder.getLocale());
            model.addAttribute("ErrorMessage", errorMessage);
        } else {
            usersService.deleteByIds(userIds);
        }
        model.addAttribute("usersList", usersService.getUsers(pageable, usersService.getAuthenticatedUser()));
        StringBuilder idsBuilder = new StringBuilder();
        for (Long id : userIds) {
            idsBuilder.append(id).append(", ");
        }
        String idsString = idsBuilder.toString().replaceAll(", $", "");
        // Agregar los IDs al mensaje de registro
        String logMessage = String.format("UserController --> /user/list/delete - IDs: %s", idsString);
        logger.info(
                logService.logRequest(logMessage, "POST", new String[]{})
        );
        return "user/list :: usersTable";
    }


}