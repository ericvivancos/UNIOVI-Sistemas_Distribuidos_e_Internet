package com.example.sdi2324entrega192.controllers;

import com.example.sdi2324entrega192.entities.Friendship;
import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.services.FriendshipService;
import com.example.sdi2324entrega192.services.LogService;
import com.example.sdi2324entrega192.services.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Controlador para manejar las operaciones relacionadas con la gestión de amigos.
 */
@Controller
public class FriendsController {
    private final FriendshipService friendShipService;
    private final UsersService usersService;
    private final LogService logService;
    Logger logger = LoggerFactory.getLogger(FriendsController.class);

    /**
     * Constructor de la clase FriendsController.
     *
     * @param friendShipService Servicio para gestionar las relaciones de amistad entre usuarios.
     * @param usersService      Servicio para gestionar los usuarios.
     * @param logService        Servicio para registrar logs.
     */
    public FriendsController(FriendshipService friendShipService, UsersService usersService,LogService logService) {
        this.friendShipService = friendShipService;
        this.usersService = usersService;
        this.logService = logService;
    }

    /**
     * Devuelve la lista de amigos del usuario
     * @param model
     * @param pageable
     * @param principal
     * @return
     */
    @RequestMapping("friends/list")
    public String getFriendsList(Model model, Pageable pageable, Principal principal){
        String email = principal.getName(); //sacar al user
        User user = usersService.getUserByEmail(email);
        Page<Friendship> friends=friendShipService.getFriendshipsForUser(pageable, user);
        model.addAttribute("friendsList", friends.getContent());
        model.addAttribute("page", friends);
        logger.info(
                logService.logRequest("FriendsController --> /friends/list","GET",new String[]{})
        );
        return "user/friends";

    }

    /**
     * Método para crear una relación de amistad entre dos usuarios.
     *
     * @param idSender   El ID del remitente de la solicitud de amistad.
     * @param idReceiver El ID del receptor de la solicitud de amistad.
     * @return Una cadena que representa la URL a la que se redireccionará el usuario.
     */
    @RequestMapping("/friends/accept/{idSender}/{idReceiver}")
    public String createFriendship(@PathVariable Long idSender, @PathVariable Long idReceiver){
        // Crear la relación de amistad entre los usuarios correspondientes
        friendShipService.createFriendship(usersService.getUser(idReceiver), usersService.getUser(idSender));
        logger.info(
                logService.logRequest("FriendsController --> /friends/list","GET",new String[]{idSender.toString(),idReceiver.toString()})
        );
        // Redirigir al usuario a la lista de amigos
        return "redirect:/friends/list";
    }
}