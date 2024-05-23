package com.example.sdi2324entrega192.entities;

import org.openqa.selenium.logging.LogType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Clase que representa un registro de log.
 */
@Entity
public class Log implements Comparable<Log>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String logType;
    private Timestamp timeDate;
    private String description;



    public enum LogTypes {
        PET,LOGIN_EX,ALTA,LOGIN_ERR,LOGOUT
    }

    public Log() {

    }
    public Log(String logType,Timestamp timestamp,String description){
        this.logType = logType;
        this.timeDate = timestamp;
        this.description = description;
    }
    @Override
    public int compareTo(Log o) {
        return timeDate.compareTo(o.timeDate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogType() {
        return logType;
    }
    public LocalDateTime stampToDate(){
        return this.timeDate.toLocalDateTime();
    }
    public void setLogType(String logType) {
        this.logType = logType;
    }

    public Timestamp getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(Timestamp timeDate) {
        this.timeDate = timeDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
