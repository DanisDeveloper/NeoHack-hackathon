package com.cmpeq0.neo360.controller;

import com.cmpeq0.neo360.view.worker.DeleteWorkerRequest;
import com.cmpeq0.neo360.view.worker.WorkerConnectionView;
import com.cmpeq0.neo360.view.worker.WorkerDataView;
import com.cmpeq0.neo360.service.WorkerService;
import com.cmpeq0.neo360.view.worker.WorkerView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService service;

    @GetMapping
    public ResponseEntity<List<WorkerDataView>> listWorkers() {
        return ResponseEntity.ok(service.listWorkers());
    }

    @PostMapping
    public ResponseEntity<?> addWorker(@RequestBody WorkerDataView request) {
        service.addWorker(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<WorkerView> deleteWorker(@RequestBody DeleteWorkerRequest request) {
        return ResponseEntity.ok(service.deleteWorker(request));
    }

    @GetMapping("/{telegramId}")
    public ResponseEntity<WorkerView> getWorker(@RequestParam String telegramId) {
        return ResponseEntity.ok(service.getWorker(telegramId));
    }

    @PostMapping("/connection")
    public ResponseEntity<?> addConnection(@RequestBody WorkerConnectionView connectionView) {
        service.connectWorkers(connectionView);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/connection")
    public ResponseEntity<?> deleteConnection(@RequestBody WorkerConnectionView connectionView) {
        service.removeWorkerConnection(connectionView);
        return ResponseEntity.ok().build();
    }


}
