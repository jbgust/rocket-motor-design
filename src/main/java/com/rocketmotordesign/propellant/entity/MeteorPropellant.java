package com.rocketmotordesign.propellant.entity;

import com.rocketmotordesign.security.models.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(
        name = "propellant",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "owner_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class MeteorPropellant {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Size(max = 256)
    private String name;

    @Size(max = 1000)
    private String description;

    @Column(name = "json_propellant", columnDefinition = "JSON")
    private String json;

    @ManyToOne
    @CreatedBy
    @JoinColumn(name = "owner_id")
    private User owner;

    public MeteorPropellant(String name, String description, String json) {
        this.name = name;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
