package com.example.sdi2324entrega192.repositories;


import com.example.sdi2324entrega192.entities.Friendship;
import com.example.sdi2324entrega192.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Interfaz para el repositorio de usuarios.
 */
public interface UsersRepository extends CrudRepository<User, Long> {

    /**
     * Encuentra un usuario por su correo electrónico.
     *
     * @param email El correo electrónico del usuario.
     * @return El usuario encontrado, o null si no se encuentra.
     */
    User findByEmail(String email);

    /**
     * Encuentra todos los usuarios.
     *
     * @param pageable La información de paginación.
     * @return Una página de todos los usuarios.
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Encuentra todos los usuarios excepto uno dado y los administradores.
     *
     * @param pageable La información de paginación.
     * @param email El correo electrónico del usuario a excluir.
     * @return Una página de usuarios sin el usuario dado y los administradores.
     */
    @Query("SELECT r FROM User r WHERE r.email != ?1 AND r.role != 'ROLE_ADMIN' ")
    Page<User> findAllUsersWithoutUser(Pageable pageable, String email);

    /**
     * Busca usuarios que coincidan con un texto de búsqueda.
     *
     * @param pageable La información de paginación.
     * @param searchText El texto de búsqueda.
     * @return Una página de usuarios que coinciden con el texto de búsqueda.
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(?1) OR LOWER(u.lastName) LIKE LOWER(?1) OR LOWER(u.email) LIKE LOWER(?1)")
    Page<User> searchUsers(Pageable pageable, String searchText);

    /**
     * Busca usuarios que coincidan con un texto de búsqueda, excluyendo uno dado y los administradores.
     *
     * @param pageable La información de paginación.
     * @param searchText El texto de búsqueda.
     * @param email El correo electrónico del usuario a excluir.
     * @return Una página de usuarios que coinciden con el texto de búsqueda, excluyendo el usuario dado y los administradores.
     */
    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE LOWER(?1) OR LOWER(u.lastName) LIKE LOWER(?1) OR LOWER(u.email) LIKE LOWER(?1))" +
            " AND u.email != ?2" +
            " AND u.role != 'ROLE_ADMIN'")
    Page<User> searchUsersWithoutUser(Pageable pageable, String searchText, String email);

    /**
     * Elimina usuarios por su ID.
     *
     * @param ids La lista de IDs de usuarios a eliminar.
     */
    @Modifying
    @Transactional
    @Query("delete from User u where u.id in(:ids)")
    void deleteByIds(List<Long> ids);
}
