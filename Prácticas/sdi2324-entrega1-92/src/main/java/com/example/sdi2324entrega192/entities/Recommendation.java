package com.example.sdi2324entrega192.entities;

import javax.persistence.*;

/**
 * Clase de entidad que representa una recomendación de un usuario para una publicación.
 */
@Entity
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Recommendation(User user, Post post) {
        this.user = user;
        this.post = post;
    }


    public Recommendation() {

    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public Post getPost() {
        return post;
    }


    public void setPost(Post post) {
        this.post = post;
    }
}
