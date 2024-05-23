package com.example.sdi2324entrega192.services;

import com.example.sdi2324entrega192.entities.Friendship;
import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.repositories.FriendshipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;


/**
 * Clase de servicio para manejar las amistades entre usuarios.
 */
@Service
public class FriendshipService {

    /** Repositorio de amistades */
    private final FriendshipRepository friendshipRepository;

    /**
     * Constructor de la clase FriendshipService.
     *
     * @param friendshipRepository El repositorio de amistades.
     */
    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Obtiene las amistades de un usuario.
     *
     * @param pageable La información de paginación.
     * @param user El usuario para el que se buscan las amistades.
     * @return Una página de amistades del usuario.
     */
    public Page<Friendship> getFriendshipsForUser(Pageable pageable, User user) {
        return friendshipRepository.findFriendsForUser(pageable, user.getEmail());
    }

    /**
     * Guarda una amistad en el repositorio si no existe previamente.
     *
     * @param fr La amistad a guardar.
     */
    public void save(Friendship fr) {
        // Comprobar que la amistad no existe previamente
        if (!existeAmistad(fr)) {
            friendshipRepository.save(fr);
        }
    }

    /**
     * Comprueba si una amistad ya existe en el repositorio.
     *
     * @param fr La amistad a comprobar.
     * @return true si la amistad ya existe; false en caso contrario.
     */
    private boolean existeAmistad(Friendship fr) {
        if (fr.getId() != null) {
            return friendshipRepository.findById(fr.getId()).isPresent();
        }
        return false;
    }

    /**
     * Crea una nueva amistad entre dos usuarios.
     *
     * @param sender El usuario que envía la solicitud de amistad.
     * @param receiver El usuario que recibe la solicitud de amistad.
     */
    public void createFriendship(User sender, User receiver) {
        // Crea una amistad desde el remitente hacia el receptor
        Friendship fsSender = new Friendship();
        fsSender.setFriendshipDate(Date.valueOf(LocalDate.now()));
        fsSender.setUser(sender);
        fsSender.setFriend(receiver);
        save(fsSender);
        sender.getFriendships().add(fsSender);

        // Crea una amistad desde el receptor hacia el remitente
        Friendship fsReceiver = new Friendship();
        fsReceiver.setFriendshipDate(Date.valueOf(LocalDate.now()));
        fsReceiver.setUser(receiver);
        fsReceiver.setFriend(sender);
        save(fsReceiver);
        receiver.getFriendships().add(fsReceiver);
    }

    /**
     * Comprueba si dos usuarios ya son amigos.
     *
     * @param sender El primer usuario.
     * @param receiver El segundo usuario.
     * @return true si los usuarios son amigos; false en caso contrario.
     */
    public boolean areAlreadyFriends(User sender, User receiver) {
        return friendshipRepository.areAlreadyFriends(sender, receiver);
    }
}
