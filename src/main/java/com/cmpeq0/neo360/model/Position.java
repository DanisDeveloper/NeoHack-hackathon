package com.cmpeq0.neo360.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private double score;

    @OneToMany
    private List<SkillRequirement> requirements;

}
