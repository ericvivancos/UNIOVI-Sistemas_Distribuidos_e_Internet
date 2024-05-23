package com.example.sdi2324entrega192.entities;

import com.example.sdi2324entrega192.services.PostStatusService;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase que representa una publicación.
 */
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String content;
    private LocalDate creationDate;
    private String status;
    private int recommendations;

    /**
     * Conjunto de recomendaciones asociadas a esta publicación.
     */
    @OneToMany(mappedBy = "post")
    private Set<Recommendation> usersRecommend = new HashSet<>();

    /**
     * Obtiene el conjunto de recomendaciones asociadas a esta publicación.
     * @return Conjunto de recomendaciones asociadas a esta publicación.
     */
    public Set<Recommendation> getUsersRecommend() {
        return usersRecommend;
    }

    private String photoUrl; // Nuevo campo para la URL de la foto

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User owner;

    public Post(String title, LocalDate creationDate, String content){
        this.title = title;
        this.creationDate = creationDate;
        this.content=content;
        //SIEMPRE QUE SE CREAN SON AceptadaS
        setStatus(PostStatusService.getStatus(PostStatusService.Aceptada));
    }

    //PARA PRUEBAS
    public Post(String title, LocalDate creationDate, String content, User owner){
        this.title = title;
        this.creationDate = creationDate;
        this.content=content;
        this.owner=owner;
        //SIEMPRE QUE SE CREAN SON AceptadaS
        setStatus(PostStatusService.getStatus(PostStatusService.Aceptada));
    }
    public Post(){
        //SIEMPRE QUE SE CREAN SON AceptadaS
        setStatus(PostStatusService.getStatus(PostStatusService.Aceptada));
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRecommendations() {return recommendations;}

    public void setRecommendations(int recommendations) {this.recommendations = recommendations;}


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
