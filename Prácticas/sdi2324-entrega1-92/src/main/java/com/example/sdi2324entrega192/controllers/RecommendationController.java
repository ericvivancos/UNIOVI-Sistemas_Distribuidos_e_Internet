package com.example.sdi2324entrega192.controllers;

import com.example.sdi2324entrega192.entities.Post;
import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controlador para manejar las solicitudes relacionadas con las recomendaciones de publicaciones.
 */
@Controller
public class RecommendationController {
    @Autowired
    private LogService logService;
    Logger logger = LoggerFactory.getLogger(FriendsController.class);
    private final RecommendationService recommendationService;
    private final UsersService usersService;
    private final PostService postService;
    private final FriendshipService friendshipService;

    /**
     * Constructor que inicializa los servicios necesarios para el controlador.
     *
     * @param recommendationService Servicio para gestionar las recomendaciones.
     * @param usersService          Servicio para gestionar los usuarios.
     * @param postService           Servicio para gestionar las publicaciones.
     * @param friendshipService     Servicio para gestionar las amistades entre usuarios.
     */
    public RecommendationController(RecommendationService recommendationService, UsersService usersService, PostService postService, FriendshipService friendshipService){
        this.recommendationService = recommendationService;
        this.usersService = usersService;
        this.postService = postService;
        this.friendshipService = friendshipService;
    }

    /**
     * Método que maneja las solicitudes HTTP dirigidas a la URL "/recommendation/{id}".
     * Si el usuario que quiere realizar la recomendación es amigo del usuario propietario
     * del post se procesa la recomendación.
     *
     * En otro caso se redirige a una página de error con un mensaje personalizado
     *
     * @param model              Objeto utilizado para pasar datos a la vista.
     * @param principal          Objeto que representa al usuario autenticado.
     * @param id                 Identificador de la publicación.
     * @return Una cadena que representa la URL a la que se redireccionará el usuario.
     */
    @RequestMapping("/recommendation/{id}")
    public String getRecommendation(Model model, Principal principal, @PathVariable Long id){
        Post post = postService.getPostById(id);
        String email= principal.getName();
        User user = usersService.getUserByEmail(email);
        if (friendshipService.areAlreadyFriends(post.getOwner(), user)){
            if (!recommendationService.alreadyRecommended(user, post)){
                recommendationService.addRecomendation(user, post);
                postService.incrementRecommendations(id);
            }
            logger.info(
                    logService.logRequest("RecommendationController --> /recommendation/"+id.toString() , "GET", new String[]{id.toString()})
            );
            return "redirect:/post/friendList/" + post.getOwner().getId();
        }
       else{
           model.addAttribute("personalizedMessage", "Error.page.recommendation");
           return "accessDenied";
        }
    }
}
