package com.example.sdi2324entrega192.services;

import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.entities.FriendRequest;
import com.example.sdi2324entrega192.repositories.FriendRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Clase de servicio para manejar las solicitudes de amistad.
 */
@Service
public class FriendRequestService {

    /** Repositorio de solicitudes de amistad */
    private final FriendRequestRepository friendRequestRepository;

    /**
     * Constructor de la clase FriendRequestService.
     *
     * @param friendRequestRepository El repositorio de solicitudes de amistad.
     */
    public FriendRequestService(FriendRequestRepository friendRequestRepository) {
        this.friendRequestRepository = friendRequestRepository;
    }

    /**
     * Obtiene las invitaciones de amistad pendientes para un usuario dado.
     *
     * @param pageable La información de paginación.
     * @param user El usuario para el que se buscan las invitaciones pendientes.
     * @return Una página de solicitudes de amistad pendientes.
     */
    public Page<FriendRequest> getPendingsInvitationsForUser(Pageable pageable, User user) {
        return friendRequestRepository.findAllPendingByUser(pageable, user);
    }

    /**
     * Comprueba si dos usuarios ya son amigos.
     *
     * @param sender El primer usuario.
     * @param receiver El segundo usuario.
     * @return true si los usuarios son amigos; false en caso contrario.
     */
    public boolean areAlreadyFriends(User sender, User receiver) {
        return friendRequestRepository.areAlreadyFriends(sender, receiver);
    }

    /**
     * Envía una solicitud de amistad desde un usuario hacia otro.
     *
     * @param from El usuario que envía la solicitud.
     * @param to El usuario al que se envía la solicitud.
     * @return true si se envía la solicitud con éxito; false si ya existe una solicitud pendiente entre los usuarios.
     */
    public boolean sendInvite(User from, User to) {
        // Verificamos que no exista ninguna solicitud pendiente entre los usuarios
        Page<FriendRequest> existingRequests = friendRequestRepository.findAllPendingByUsers(Pageable.unpaged(), from, to);
        if (existingRequests.getTotalElements() > 0) {
            return false;
        }

        // Creamos una nueva solicitud de amistad y la guardamos en el repositorio
        FriendRequest newRequest = new FriendRequest(from, to, LocalDateTime.now(), FriendRequest.RequestStatus.PENDING);
        friendRequestRepository.save(newRequest);
        return true;
    }

    /**
     * Actualiza el estado de una solicitud de amistad a 'ACEPTADA'.
     *
     * @param id El ID de la solicitud de amistad.
     */
    public void updateStateToAccept(Long id) {
        friendRequestRepository.updateStateToAccept(id);
    }

    /**
     * Encuentra una solicitud de amistad por su ID.
     *
     * @param id El ID de la solicitud de amistad.
     * @return La solicitud de amistad encontrada, o null si no se encuentra.
     */
    public FriendRequest findFriendRequestById(Long id) {
        return friendRequestRepository.findFriendRequestById(id);
    }
}

