package com.cmpeq0.neo360.dao;

import com.cmpeq0.neo360.model.Position;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface PositionRepository extends CrudRepository<Position, Long> {

    Position findPositionByName(String name);

}
