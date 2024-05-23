package com.example.sdi2324entrega192.repositories;

import com.example.sdi2324entrega192.entities.Post;
import com.example.sdi2324entrega192.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Interfaz para el repositorio de publicaciones.
 */
public interface PostRepository extends CrudRepository<Post, Long> {

    /**
     * Elimina todas las publicaciones de un propietario específico.
     *
     * @param owner El propietario de las publicaciones.
     */
    @Transactional
    void deleteByOwner(User owner);

    /**
     * Encuentra todas las publicaciones de un usuario propietario ordenadas por fecha de creación de manera descendente, excluyendo las censuradas.
     *
     * @param user El usuario propietario de las publicaciones.
     * @param pageable La información de paginación.
     * @return Una página de publicaciones del usuario.
     */
    @Query("SELECT p from Post p WHERE p.owner = ?1 and p.status != 'Censurada' ORDER BY p.creationDate DESC")
    Page<Post> findAllByOwner(User user, Pageable pageable);

    /**
     * Encuentra todas las publicaciones para un amigo dado, excluyendo las censuradas.
     *
     * @param user El usuario amigo.
     * @param pageable La información de paginación.
     * @return Una página de publicaciones para el amigo.
     */
    @Query("SELECT p from Post p WHERE p.owner = ?1 and p.status = 'Aceptada' ORDER BY p.creationDate DESC")
    Page<Post> findAllForFriend(User user, Pageable pageable);

    /**
     * Encuentra todas las publicaciones ordenadas por fecha de creación de manera descendente.
     *
     * @param pageable La información de paginación.
     * @return Una página de todas las publicaciones.
     */
    @Query("SELECT p FROM Post p  ORDER BY p.creationDate DESC")
    Page<Post> findAll(Pageable pageable);

    /**
     * Encuentra todas las publicaciones que coinciden con una palabra clave, ordenadas por fecha de creación de manera descendente.
     *
     * @param keyword La palabra clave de búsqueda.
     * @param pageable La información de paginación.
     * @return Una página de publicaciones que coinciden con la palabra clave.
     */
    @Query("SELECT p FROM Post p WHERE (UPPER(p.title) LIKE UPPER(CONCAT('%', ?1, '%')) OR UPPER(p.owner.email) LIKE UPPER(CONCAT('%', ?1, '%')) OR UPPER(p.status) LIKE UPPER(CONCAT('%', ?1, '%'))) ORDER BY p.creationDate DESC")
    Page<Post> findAllSearch(String keyword, Pageable pageable);

    /**
     * Encuentra todas las publicaciones para un amigo dado que coinciden con una palabra clave, ordenadas por fecha de creación de manera descendente.
     *
     * @param user El usuario amigo.
     * @param keyword La palabra clave de búsqueda.
     * @param pageable La información de paginación.
     * @return Una página de publicaciones para el amigo que coinciden con la palabra clave.
     */
    @Query("SELECT p FROM Post p WHERE p.owner = ?1 AND p.status = 'Aceptada' AND (UPPER(p.title) LIKE UPPER(CONCAT('%', ?2, '%')) OR UPPER(p.owner.email) LIKE UPPER(CONCAT('%', ?2, '%')) OR UPPER(p.status) LIKE UPPER(CONCAT('%', ?2, '%'))) ORDER BY p.creationDate DESC")
    Page<Post> findAllForFriendSearch(User user, String keyword, Pageable pageable);

    /**
     * Encuentra todas las publicaciones de un usuario propietario que coinciden con una palabra clave, ordenadas por fecha de creación de manera descendente.
     *
     * @param user El usuario propietario de las publicaciones.
     * @param keyword La palabra clave de búsqueda.
     * @param pageable La información de paginación.
     * @return Una página de publicaciones del usuario que coinciden con la palabra clave.
     */
    @Query("SELECT p FROM Post p WHERE p.owner = ?1 AND p.status != 'Censurada' AND (UPPER(p.title) LIKE UPPER(CONCAT('%', ?2, '%')) OR UPPER(p.owner.email) LIKE UPPER(CONCAT('%', ?2, '%')) OR UPPER(p.status) LIKE UPPER(CONCAT('%', ?2, '%'))) ORDER BY p.creationDate DESC")
    Page<Post> findAllByOwnerSearch(User user, String keyword, Pageable pageable);

    /**
     * Incrementa el número de recomendaciones de una publicación.
     *
     * @param post_id El ID de la publicación.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.recommendations = p.recommendations + 1 WHERE p.id = ?1")
    void incrementRecommendations(Long post_id);
}
