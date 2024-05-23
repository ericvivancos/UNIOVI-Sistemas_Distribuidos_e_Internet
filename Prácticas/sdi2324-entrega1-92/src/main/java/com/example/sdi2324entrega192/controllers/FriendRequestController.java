package com.example.sdi2324entrega192.controllers;

import com.example.sdi2324entrega192.entities.FriendRequest;
import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.services.FriendRequestService;
import com.example.sdi2324entrega192.services.LogService;
import com.example.sdi2324entrega192.services.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controlador encargado de gestionar las solicitudes de amistad entre usuarios.
 */
@Controller
public class FriendRequestController {
    @Autowired
    private MessageSource messageSource;
    private final FriendRequestService friendRequestService;
    private final UsersService usersService;
    private final LogService logService;
    Logger logger = LoggerFactory.getLogger(FriendRequestController.class);

    /**
     * Constructor de la clase FriendRequestController.
     *
     * @param friendRequestService Servicio para gestionar las solicitudes de amistad.
     * @param usersService         Servicio para gestionar los usuarios.
     * @param logService           Servicio para registrar logs.
     */
    public FriendRequestController(FriendRequestService friendRequestService, UsersService usersService,LogService logService) {
        this.friendRequestService = friendRequestService;
        this.usersService = usersService;
        this.logService = logService;
    }

    /**
     * Método que devuelve la lista de invitaciones para el usuario regsitrado
     * @param model
     * @param pageable
     * @param principal
     * @return
     */
    @RequestMapping("/friendRequest/list")
    //principal es el usuario loggin
    public String getInvitations(Model model, Pageable pageable, Principal principal) {
        String email = principal.getName(); // email es el user autenticado , lo sacas
        User user = usersService.getUserByEmail(email);
        Page<FriendRequest> invitations;
        invitations = friendRequestService.getPendingsInvitationsForUser(pageable, user);
        //ahora tras usar las pagiunas a la vista le pasas la pagina como tal
        model.addAttribute("friendRequestList", invitations.getContent());
        model.addAttribute("page", invitations);
        logger.info(
                logService.logRequest("FriendRequestController --> /friendRequest/list","GET",new String[]{})
        );
        return "friendRequest/list";
    }

    /**
     * Método para enviar una solicitud de amistad.
     *
     * @param model             El modelo que se utilizará para pasar datos a la vista.
     * @param principal         El principal del usuario autenticado.
     * @param id                El ID del usuario al que se enviará la solicitud de amistad.
     * @param redirectAttributes Atributos para redireccionar después de enviar la solicitud.
     * @return La URL a la que se redireccionará el usuario.
     */
    @RequestMapping(value = "/invite/send/{id}", method = RequestMethod.POST)
    public String sendFriendRequest(Model model, Principal principal, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Obtener el correo electrónico del usuario autenticado
            String email = principal.getName();

            // Obtener el usuario remitente y receptor de la solicitud de amistad
            User sender = usersService.getUserByEmail(email);
            User receiver = usersService.getUser(id);

            // Verificar si los usuarios ya son amigos
            boolean alreadyFriends = friendRequestService.areAlreadyFriends(sender, receiver);

            // Enviar la solicitud de amistad y verificar si fue exitosa
            if (!friendRequestService.sendInvite(sender, receiver)) {
                // Si ya existe una solicitud pendiente, agregar un mensaje de error a los atributos de redirección
                String errorMessage = messageSource.getMessage("Error.existsfriendRequest", null, LocaleContextHolder.getLocale());
                redirectAttributes.addFlashAttribute("ErrorMessage", errorMessage);
                redirectAttributes.addFlashAttribute("alreadyFriends", alreadyFriends);
            }
            // Registrar la solicitud de amistad enviada en los logs
            logger.info(
                    logService.logRequest("FriendRequestController --> /invite/send/" + id + "/", "POST", new String[]{id.toString()})
            );
            // Redirigir al usuario a la lista de usuarios después de enviar la solicitud de amistad
            return "redirect:/user/list";
        } catch (Exception e) {
            // Manejar cualquier excepción que pueda ocurrir durante el envío de la solicitud de amistad
            logger.error("Error al enviar la solicitud de amistad: " + e.getMessage());
            return "redirect:/error";
        }
    }

    /**
     * Método para aceptar una solicitud de amistad.
     *
     * @param id El ID de la solicitud de amistad que se desea aceptar.
     * @return Una cadena que representa la URL a la que se redireccionará el usuario.
     */
    @RequestMapping("/friendRequest/accept/{id}")
    public String acceptInvitation(@PathVariable Long id){
        // Obtener la solicitud de amistad correspondiente al ID proporcionado
        FriendRequest friendRequest = friendRequestService.findFriendRequestById(id);

        // Obtener los IDs del remitente y el receptor de la solicitud de amistad
        long senderId = friendRequest.getSender().getId();
        long receiverId = friendRequest.getReceiver().getId();

        // Actualizar el estado de la solicitud de amistad a "aceptada"
        friendRequestService.updateStateToAccept(id);

        logger.info(
                logService.logRequest("FriendRequestController --> /friendRequest/accept/" + id + "/", "POST", new String[]{id.toString()})
        );
        // Redirigir al usuario a la página de aceptación de la solicitud de amistad
        return "redirect:/friends/accept/" + senderId + "/" + receiverId;
    }

}