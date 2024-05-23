package com.example.sdi2324entrega192.entities;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 * Entidad que representa una solicitud de amistad.
 */
@Entity
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime date;

    /**
     * Constructor de la clase FriendRequest.
     *
     * @param from   Usuario que envía la solicitud.
     * @param to     Usuario que recibe la solicitud.
     * @param date   Fecha y hora de la solicitud.
     * @param status Estado de la solicitud.
     */
    public FriendRequest(User from, User to, LocalDateTime date, RequestStatus status) {
        this.sender = from;
        this.receiver = to;
        this.date = date;
        this.status = status;
    }

    public FriendRequest() {
    }

    // Otros campos y métodos getters/setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    /**
     * Enumeración que representa los estados de una solicitud de amistad.
     */
    public enum RequestStatus {
        PENDING,
        ACCEPTED
    }

    /**
     * Método que devuelve la fecha formateada de la solicitud.
     *
     * @return Fecha formateada.
     */
    public Date getFechaFormateada() {
        return Date.valueOf(date.toLocalDate());
    }
}
