package com.rocketmotordesign.motor.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "motor")
public class Motor {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    @Column(name = "json_motor", columnDefinition = "JSON")
    private String json;

    public Motor(UUID id, String name, String description, String json) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.json = json;
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
}
