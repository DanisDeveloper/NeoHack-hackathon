package com.cmpeq0.neo360.dao;

import com.cmpeq0.neo360.model.SkillRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface SkillRecordRepository extends CrudRepository<SkillRecord, Long> {
}
