package com.example.sdi2324entrega192.entities;

import javax.persistence.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase que representa un usuario en el sistema.
 */
@Entity
@Table(name = "user")
public class User  {
    @Id
    @GeneratedValue
    private long id;
    @Column(unique = true)
    private String email;
    private String name;
    private String lastName;
    private String role;
    private String password;
    @Transient
    private String passwordConfirm;


    /**
     * Relación uno a muchos con la entidad Friendship. Representa las amistades de este usuario.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Friendship> friendships = new HashSet<>();

    /**
     * Relación uno a muchos con la entidad Recommendation. Representa las publicaciones recomendadas para este usuario.
     */
    @OneToMany(mappedBy = "user")
    private Set<Recommendation> recommendedPosts = new HashSet<>();

    /**
     * Obtiene las publicaciones recomendadas para este usuario.
     * @return Un conjunto de objetos Recommendation que representan las publicaciones recomendadas para este usuario.
     */
    public Set<Recommendation> getRecommendedPosts(){
        return recommendedPosts;
    }

    /**
     * Relación uno a muchos con la entidad Post. Representa las publicaciones creadas por este usuario.
     * Se completará cuando se implemente la funcionalidad correspondiente.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Post> posts = new HashSet<>();

    /**
     * Obtiene las amistades de este usuario.
     * @return Un conjunto de objetos Friendship que representan las amistades de este usuario.
     */
    public Set<Friendship> getFriendships() {
        return friendships;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }



    public User(String email, String name,String lastname){
        super();
        this.email = email;
        this.name= name;
        this.lastName = lastname;
    }

    public User(String email, String name,String lastname, String role){
        super();
        this.email = email;
        this.name= name;
        this.lastName = lastname;
        this.role = role;
    }
    public User(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName(){
        return this.name+" "+this.lastName;
    }

    public void setFriendships(Set<Friendship> friendships) {
        this.friendships = friendships;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.email.equals(user.email);
    }

    /**
     * Devuelve la última publicación realizada por este usuario.
     * @return La última publicación del usuario, o null si no hay ninguna.
     */
    public Post lastPost(){
        return this.posts.stream().max(Comparator.comparing(Post::getCreationDate)).orElse(null);
    }

    /**
     * Devuelve una cadena de texto que contiene toda la información del usuario.
     * @return Información del usuario en formato "nombre apellido, correo electrónico".
     */
    public String getAllInfo(){
        return this.name+" "+this.lastName + ", " + this.email;
    }

    /**
     * Verifica si este usuario puede recibir una solicitud de amistad de otro usuario dado su correo electrónico.
     * @param email El correo electrónico del usuario que envía la solicitud de amistad.
     * @return true si este usuario puede recibir la solicitud de amistad, false de lo contrario.
     */
    public boolean canReceiveFriendshipInvite(String email){
        for (Friendship friendship : friendships) {
            if (friendship.getFriend().getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }
}
