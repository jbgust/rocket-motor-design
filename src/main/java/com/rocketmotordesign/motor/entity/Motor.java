package com.rocketmotordesign.motor.entity;

import com.rocketmotordesign.security.models.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "motor")
@EntityListeners(AuditingEntityListener.class)
public class Motor {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    @ManyToOne
    @CreatedBy
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "json_motor", columnDefinition = "JSON")
    private String json;

    public Motor(UUID id, String name, String description, String json, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.json = json;
        this.owner = owner;
    }

    protected Motor() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
