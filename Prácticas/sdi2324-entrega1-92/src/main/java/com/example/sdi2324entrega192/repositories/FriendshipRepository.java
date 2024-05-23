package com.example.sdi2324entrega192.repositories;

import com.example.sdi2324entrega192.entities.Friendship;
import com.example.sdi2324entrega192.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Interfaz para el repositorio de amistades.
 */
public interface FriendshipRepository extends CrudRepository<Friendship, Long> {

    /**
     * Encuentra las amistades de un usuario dado.
     *
     * @param pageable La información de paginación.
     * @param userEmail El correo electrónico del usuario.
     * @return Una página de amistades del usuario.
     */
    @Query("SELECT f " +
            "FROM Friendship f " +
            "WHERE (f.user.email = :userEmail AND f.friend.email != :userEmail) ")
    Page<Friendship> findFriendsForUser(Pageable pageable, String userEmail);

    /**
     * Comprueba si dos usuarios son amigos.
     *
     * @param user1 El primer usuario.
     * @param user2 El segundo usuario.
     * @return true si los usuarios son amigos; false en caso contrario.
     */
    @Query("SELECT COUNT(r) > 0 FROM Friendship r " +
            "WHERE (r.user = :user1 AND r.friend = :user2) " +
            "OR (r.user = :user2 AND r.friend = :user1)")
    boolean areAlreadyFriends(@Param("user1") User user1, @Param("user2") User user2);

    /**
     * Elimina todas las amistades de un usuario.
     *
     * @param user El usuario cuyas amistades se eliminarán.
     */
    @Transactional
    void deleteByUser(User user);

    /**
     * Elimina todas las amistades en las que un usuario es amigo.
     *
     * @param user El usuario que es amigo.
     */
    @Transactional
    void deleteByFriend(User user);
}
