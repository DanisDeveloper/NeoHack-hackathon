package com.cmpeq0.neo360.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private Worker source;

    @OneToOne
    private Worker target;

    @ManyToOne
    private Skill targetSkill;

    private int score;

}
