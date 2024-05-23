package com.example.sdi2324entrega192.entities;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 * clase que repsenta la amitad entre varios usuarios , contene la fecha en la que se acepto la solicutd de amistad
 * representa la relacion refelxiva n a n con el atributo date del modelo
 */
@Entity
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    private Date friendshipDate;

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

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public Date getFriendshipDate() {
        return friendshipDate;
    }

    public void setFriendshipDate(Date friendshipDate) {
        this.friendshipDate = friendshipDate;
    }
}