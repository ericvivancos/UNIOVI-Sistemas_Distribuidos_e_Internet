package com.example.sdi2324entrega192.services;

import com.example.sdi2324entrega192.entities.Post;
import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.repositories.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Clase de servicio para manejar las publicaciones.
 */
@Service
public class PostService {

    /** Repositorio de publicaciones */
    private final PostRepository postRepository;

    /** Servicio de roles */
    private final RolesService rolesService;

    /**
     * Constructor de la clase PostService.
     *
     * @param postRepository El repositorio de publicaciones.
     * @param rolesService El servicio de roles.
     */
    public PostService(PostRepository postRepository, RolesService rolesService){
        this.postRepository = postRepository;
        this.rolesService = rolesService;
    }

    /**
     * Agrega una nueva publicación.
     *
     * @param post La publicación a agregar.
     */
    public void addNewPost(Post post){
        postRepository.save(post);
    }

    /**
     * Obtiene las publicaciones de un usuario.
     *
     * @param pageable La información de paginación.
     * @param user El usuario del que se buscan las publicaciones.
     * @return Una página de publicaciones del usuario.
     */
    public Page<Post> getPostsOfUser(Pageable pageable, User user) {
        return postRepository.findAllByOwner(user, pageable);
    }

    /**
     * Obtiene las publicaciones de los amigos de un usuario.
     *
     * @param pageable La información de paginación.
     * @param user El usuario del que se buscan las publicaciones de amigos.
     * @return Una página de publicaciones de los amigos del usuario.
     */
    public Page<Post> getPostsOfFriend(Pageable pageable, User user) {
        return postRepository.findAllForFriend(user, pageable);
    }

    /**
     * Obtiene todas las publicaciones.
     *
     * @param pageable La información de paginación.
     * @param user El usuario que realiza la solicitud.
     * @return Una página de todas las publicaciones.
     */
    public Page<Post> getAll(Pageable pageable, User user) {
        if (rolesService.getRoles()[RolesService.ADMIN].equals(user.getRole())) {
            return postRepository.findAll(pageable);
        }
        return null; // Devuelve null si el usuario no es administrador
    }

    /**
     * Obtiene una publicación por su ID.
     *
     * @param id El ID de la publicación.
     * @return La publicación encontrada, o una publicación vacía si no se encuentra.
     */
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(new Post());
    }

    /**
     * Recupera publicaciones basadas en un filtro de búsqueda y una palabra clave.
     *
     * @param keyword La palabra clave a buscar.
     * @param pageable La información de paginación.
     * @param user El usuario que realiza la búsqueda.
     * @param searchType El tipo de búsqueda (usuario, amigos, todas).
     * @return Una página de publicaciones que coinciden con los criterios de búsqueda.
     */
    public Page<Post> getPostBySearchViewWithFilter(String keyword, Pageable pageable, User user, String searchType) {
        if (searchType.equals("all") && rolesService.getRoles()[RolesService.ADMIN].equals(user.getRole())) {
            return postRepository.findAllSearch(keyword, pageable);
        } else if (searchType.equals("user")) {
            return postRepository.findAllByOwnerSearch(user, keyword, pageable);
        } else if (searchType.equals("friends")) {
            return postRepository.findAllForFriendSearch(user, keyword, pageable);
        }
        return null; // Devuelve null si el tipo de búsqueda no es válido
    }

    /**
     * Recupera publicaciones basadas en una vista de búsqueda.
     *
     * @param pageable La información de paginación.
     * @param user El usuario que realiza la búsqueda.
     * @param currentView La vista de búsqueda actual (usuario, amigos, todas).
     * @return Una página de publicaciones que coinciden con los criterios de búsqueda.
     */
    public Page<Post> getPostBySearchView(Pageable pageable, User user, String currentView) {
        if (currentView.equals("all") && rolesService.getRoles()[RolesService.ADMIN].equals(user.getRole())) {
            return postRepository.findAll(pageable);
        } else if (currentView.equals("user")) {
            return postRepository.findAllByOwner(user, pageable);
        } else if (currentView.equals("friends")) {
            return postRepository.findAllForFriend(user, pageable);
        }
        return null; // Devuelve null si la vista de búsqueda no es válida
    }

    /**
     * Incrementa las recomendaciones de una publicación.
     *
     * @param postId El ID de la publicación.
     */
    public void incrementRecommendations(Long postId) {
        postRepository.incrementRecommendations(postId);
    }
}