package com.cmpeq0.neo360.service;

import com.cmpeq0.neo360.dao.EdgeRepository;
import com.cmpeq0.neo360.model.Edge;
import com.cmpeq0.neo360.model.Worker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EdgeService {

    private final EdgeRepository edgeRepository;

    public List<Worker> findAllTargets(Worker source) {
        List<Edge> edges = edgeRepository.findAllBySourceOrTarget(source, source);
        List<Worker> result = new ArrayList<>();
        for (var current : edges) {
            if (current.getSource().getId() == source.getId()) {
                result.add(current.getTarget());
            } else {
                result.add(current.getSource());
            }
        }
        return result;
    }

}
