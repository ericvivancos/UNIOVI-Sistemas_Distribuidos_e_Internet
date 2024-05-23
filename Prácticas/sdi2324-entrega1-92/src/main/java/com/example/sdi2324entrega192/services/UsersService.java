package com.example.sdi2324entrega192.services;

import com.example.sdi2324entrega192.entities.Friendship;
import com.example.sdi2324entrega192.repositories.*;
import com.example.sdi2324entrega192.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;


/**
 * Clase de servicio para manejar usuarios.
 */
@Service
public class UsersService {

    /** Repositorio de usuarios */
    private final UsersRepository usersRepository;

    /** Codificador de contraseñas BCrypt */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /** Repositorio de solicitudes de amistad */
    @Autowired
    private FriendRequestRepository friendsRequestRepository;

    /** Repositorio de amistades */
    @Autowired
    private FriendshipRepository friendshipRepository;

    /** Repositorio de publicaciones */
    @Autowired
    private PostRepository postRepository;

    /** Repositorio de recomendaciones */
    @Autowired
    private RecommendationRepository recommendationRepository;

    /**
     * Constructor de la clase UsersService.
     *
     * @param usersRepository El repositorio de usuarios.
     * @param bCryptPasswordEncoder El codificador de contraseñas BCrypt.
     */
    @Autowired
    public UsersService(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Método para inicializar el servicio.
     */
    @PostConstruct
    public void init(){

    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id El ID del usuario.
     * @return El usuario encontrado.
     */
    public User getUser(Long id){
        return usersRepository.findById(id).get();
    }

    /**
     * Añade un nuevo usuario.
     *
     * @param user El usuario a añadir.
     */
    public void addUser(User user){
        if(user.getPassword() != null){
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        usersRepository.save(user);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id El ID del usuario a eliminar.
     */
    public void deleteUser(Long id){
        usersRepository.deleteById(id);
    }

    /**
     * Obtiene un usuario por su correo electrónico.
     *
     * @param email El correo electrónico del usuario.
     * @return El usuario encontrado.
     */
    public User getUserByEmail(String email){
        return usersRepository.findByEmail(email);
    }

    /**
     * Actualiza la información de un usuario.
     *
     * @param user El usuario con la información actualizada.
     */
    public void updateUser(User user){
        User currentUser = getUser(user.getId());
        if(!currentUser.equals(null)){
            currentUser.setEmail(user.getEmail());
            currentUser.setName(user.getName());
            currentUser.setLastName(user.getLastName());
            currentUser.setRole(user.getRole());
            usersRepository.save(currentUser);
        }
    }

    /**
     * Obtiene una página de usuarios.
     *
     * @param pageable Objeto Pageable para la paginación.
     * @param user El usuario actual.
     * @return Una página de usuarios.
     */
    public Page<User> getUsers(Pageable pageable, User user){
        if(user.getRole().equals("ROLE_ADMIN")){
            return usersRepository.findAll(pageable);
        }else{
            return usersRepository.findAllUsersWithoutUser(pageable, user.getEmail());
        }
    }

    /**
     * Obtiene una página de usuarios según un criterio de búsqueda.
     *
     * @param pageable Objeto Pageable para la paginación.
     * @param searchText El texto de búsqueda.
     * @param user El usuario actual.
     * @return Una página de usuarios que coinciden con el criterio de búsqueda.
     */
    public Page<User> getUsersByNameLastnameAndEmail(Pageable pageable, String searchText, User user){
        searchText = "%"+searchText+"%";
        if(user.getRole().equals("ROLE_ADMIN")){
            return usersRepository.searchUsers(pageable, searchText);
        }else{
            return usersRepository.searchUsersWithoutUser(pageable, searchText, user.getEmail());
        }
    }

    /**
     * Obtiene el usuario autenticado.
     *
     * @return El usuario autenticado.
     */
    public User getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return getUserByEmail(auth.getName());
    }

    /**
     * Elimina usuarios por sus IDs y elimina todos los datos relacionados a esos usuarios.
     *
     * @param ids Lista de IDs de usuarios a eliminar.
     */
    public void deleteByIds(List<Long> ids){
        for(Long id : ids){
            User user = this.getUser(id);
            friendsRequestRepository.deleteByReceiver(user);
            friendsRequestRepository.deleteBySender(user);
            friendshipRepository.deleteByUser(user);
            friendshipRepository.deleteByFriend(user);
            postRepository.deleteByOwner(user);
            recommendationRepository.deleteByUser(user);
        }
        usersRepository.deleteByIds(ids);
    }
}