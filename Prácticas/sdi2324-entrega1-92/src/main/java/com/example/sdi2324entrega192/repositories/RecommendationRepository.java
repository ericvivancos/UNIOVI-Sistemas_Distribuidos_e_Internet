package com.example.sdi2324entrega192.repositories;

import com.example.sdi2324entrega192.entities.Post;
import com.example.sdi2324entrega192.entities.Recommendation;
import com.example.sdi2324entrega192.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;


/**
 * Interfaz de repositorio para acceder y manipular las recomendaciones de usuarios.
 */
public interface RecommendationRepository extends CrudRepository<Recommendation, Long> {

    /**
     * Método para verificar si un usuario ya ha recomendado una publicación.
     *
     * @param user El usuario que realiza la recomendación.
     * @param post La publicación que se recomienda.
     * @return true si el usuario ya ha recomendado la publicación, false en caso contrario.
     */
    @Query("SELECT count(r) > 0 FROM Recommendation r WHERE r.user = ?1 AND r.post = ?2")
    boolean alreadyRecommended(User user, Post post);

    /**
     * Método para encontrar las publicaciones recomendadas por un usuario.
     *
     * @param pageable La información de paginación.
     * @param user     El usuario cuyas recomendaciones se desean encontrar.
     * @return Una página de publicaciones recomendadas por el usuario.
     */
    @Query("SELECT r.post FROM Recommendation r WHERE r.user = ?1")
    Page<Post> findByUser(Pageable pageable, User user);

    /**
     * Método para eliminar todas las recomendaciones realizadas por un usuario.
     *
     * @param user El usuario cuyas recomendaciones se desean eliminar.
     */
    @Transactional
    void deleteByUser(User user);
}
