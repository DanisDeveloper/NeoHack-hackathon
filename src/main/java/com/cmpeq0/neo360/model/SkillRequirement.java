package com.cmpeq0.neo360.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SkillRequirement {

    public enum SkillPriority {
        HIGH,
        MEDIUM,
        LOW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Position position;

    @ManyToOne
    private Skill skill;

    private int level;

    @Enumerated
    private SkillPriority priority;
}
