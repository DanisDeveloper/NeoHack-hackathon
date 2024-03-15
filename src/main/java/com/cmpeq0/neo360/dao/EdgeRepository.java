package com.cmpeq0.neo360.dao;

import com.cmpeq0.neo360.model.Edge;
import com.cmpeq0.neo360.model.Worker;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EdgeRepository extends CrudRepository<Edge, Long> {

    List<Edge> findAllBySourceOrTarget(Worker source, Worker target);

    Edge findEdgeBySourceAndTarget(Worker source, Worker target);

}
