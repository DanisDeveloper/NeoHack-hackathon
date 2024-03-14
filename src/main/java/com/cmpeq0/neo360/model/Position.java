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

    @OneToMany
    private List<Skill> highPrioritySkill;

    @OneToMany
    private List<Skill> mediumPrioritySkill;

    @OneToMany
    private List<Skill> lowPrioritySkill;

    public Map<Skill, Skill.SkillPriority> getSkillPriorities() {
        Map<Skill, Skill.SkillPriority> result = new HashMap<>();
        highPrioritySkill.forEach((skill -> result.put(skill, Skill.SkillPriority.HIGH)));
        mediumPrioritySkill.forEach((skill -> result.put(skill, Skill.SkillPriority.MEDIUM)));
        lowPrioritySkill.forEach((skill -> result.put(skill, Skill.SkillPriority.LOW)));
        return result;
    }

}
