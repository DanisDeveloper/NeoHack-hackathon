package com.cmpeq0.neo360.controller;

import com.cmpeq0.neo360.service.PositionService;
import com.cmpeq0.neo360.view.position.DeletePositionRequest;
import com.cmpeq0.neo360.view.position.PositionView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/position")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService service;

    @GetMapping
    public ResponseEntity<List<PositionView>> listPositions() {
        return ResponseEntity.ok(service.listPositions());
    }

    @PostMapping
    public ResponseEntity<?> createPosition(@RequestBody PositionView positionView) {
        service.createPosition(positionView);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<PositionView> deletePosition(@RequestBody DeletePositionRequest request) {
        return ResponseEntity.ok(service.deletePosition(request));
    }

}
