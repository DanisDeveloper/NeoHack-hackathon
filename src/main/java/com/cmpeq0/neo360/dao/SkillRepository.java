package com.cmpeq0.neo360.dao;

import com.cmpeq0.neo360.model.Skill;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface SkillRepository extends CrudRepository<Skill, Long> {
}
