package com.example.sdi2324entrega192.repositories;

import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.entities.FriendRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Interfaz para el repositorio de solicitudes de amistad.
 */
public interface FriendRequestRepository extends CrudRepository<FriendRequest, Long> {

    /**
     * Elimina todas las solicitudes de amistad recibidas por un usuario.
     *
     * @param receiver El usuario receptor de las solicitudes.
     */
    @Transactional
    void deleteByReceiver(User receiver);

    /**
     * Elimina todas las solicitudes de amistad enviadas por un usuario.
     *
     * @param sender El usuario emisor de las solicitudes.
     */
    @Transactional
    void deleteBySender(User sender);

    /**
     * Encuentra todas las solicitudes de amistad pendientes para un usuario dado.
     *
     * @param pageable La información de paginación.
     * @param user El usuario para el que se buscan las solicitudes pendientes.
     * @return Una página de solicitudes de amistad pendientes.
     */
    @Query("SELECT r FROM FriendRequest r WHERE r.receiver = :user AND r.status = 'PENDING' ORDER BY r.id ASC")
    Page<FriendRequest> findAllPendingByUser(Pageable pageable, User user);

    /**
     * Encuentra todas las solicitudes de amistad pendientes entre dos usuarios dados.
     *
     * @param pageable La información de paginación.
     * @param sender El primer usuario.
     * @param receiver El segundo usuario.
     * @return Una página de solicitudes de amistad pendientes entre los usuarios.
     */
    @Query("SELECT r FROM FriendRequest r WHERE (r.sender = :sender AND r.receiver = :receiver) OR (r.sender = :receiver AND r.receiver = :sender) AND r.status = 'PENDING' ORDER BY r.id ASC")
    Page<FriendRequest> findAllPendingByUsers(Pageable pageable, User sender, User receiver);

    /**
     * Comprueba si dos usuarios son amigos.
     *
     * @param user1 El primer usuario.
     * @param user2 El segundo usuario.
     * @return true si los usuarios son amigos; false en caso contrario.
     */
    @Query("SELECT COUNT(r) > 0 FROM FriendRequest r " +
            "WHERE (r.sender = :user1 AND r.receiver = :user2) " +
            "OR (r.sender = :user2 AND r.receiver = :user1) " +
            "AND r.status = 'ACCEPTED'")
    boolean areAlreadyFriends(@Param("user1") User user1, @Param("user2") User user2);

    /**
     * Actualiza el estado de una solicitud de amistad a 'ACEPTADA'.
     *
     * @param id El ID de la solicitud de amistad.
     */
    @Modifying
    @Transactional
    @Query("UPDATE FriendRequest r SET r.status = 'ACCEPTED' WHERE r.id = ?1")
    void updateStateToAccept(long id);

    /**
     * Encuentra una solicitud de amistad por su ID.
     *
     * @param id El ID de la solicitud de amistad.
     * @return La solicitud de amistad encontrada, o null si no se encuentra.
     */
    FriendRequest findFriendRequestById(Long id);
}

