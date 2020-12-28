package com.rocketmotordesign.propellant.entity;

import org.hibernate.id.UUIDGenerationStrategy;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "propellant")
public class MeteorPropellant {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    private String description;

    @Column(name = "json_propellant", columnDefinition = "JSON")
    private String json;

    public MeteorPropellant(UUID id, String title, String description, String json) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.json = json;
    }

    private MeteorPropellant() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
