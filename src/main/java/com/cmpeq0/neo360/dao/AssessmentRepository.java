package com.cmpeq0.neo360.dao;

import com.cmpeq0.neo360.model.Assessment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface AssessmentRepository extends CrudRepository<Assessment, Long> {
}
