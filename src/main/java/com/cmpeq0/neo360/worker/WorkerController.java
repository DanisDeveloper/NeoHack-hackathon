package com.cmpeq0.neo360.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/worker")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService service;


    @PostMapping
    public ResponseEntity<?> addWorker(@RequestBody WorkerDataRequest request) {
        service.addWorker(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<WorkerView> deleteWorker(@RequestBody DeleteWorkerRequest request) {
        return ResponseEntity.ok(service.deleteWorker(request));
    }


}
