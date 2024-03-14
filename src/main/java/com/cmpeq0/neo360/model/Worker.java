package com.cmpeq0.neo360.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Worker {

    public enum Role {
        ADMIN,
        USER,
        UNKNOWN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Position position;

    private String telegramId;

    private String firstName;

    private String lastName;

    private double karma;

    @Enumerated
    private Role role;

    @ManyToMany
    private List<Worker> colleagues;

    @OneToMany
    private List<SkillRecord> skillRecords;


}
