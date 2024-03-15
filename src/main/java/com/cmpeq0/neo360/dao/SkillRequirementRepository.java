package com.cmpeq0.neo360.dao;

import com.cmpeq0.neo360.model.SkillRequirement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface SkillRequirementRepository extends CrudRepository<SkillRequirement, Long> {
}
