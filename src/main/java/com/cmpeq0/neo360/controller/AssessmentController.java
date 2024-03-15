package com.cmpeq0.neo360.controller;

import com.cmpeq0.neo360.model.Survey;
import com.cmpeq0.neo360.service.AssessmentService;
import com.cmpeq0.neo360.view.assessment.GetWorkerMatrixRequest;
import com.cmpeq0.neo360.view.assessment.WorkerMatrixResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping
    public ResponseEntity<Survey> getCurrentAssessment() {
        return ResponseEntity.ok(assessmentService.getCurrentAssessment());
    }

    @PostMapping("/start")
    public ResponseEntity<Survey> startAssessment() {
        return ResponseEntity.ok(assessmentService.activateNewAssessment());
    }

    @GetMapping("/matrix")
    public ResponseEntity<WorkerMatrixResponse> getWorkerMatrix(@RequestBody GetWorkerMatrixRequest request) {
        return ResponseEntity.ok(assessmentService.assessWorker(request));
    }

    @GetMapping("/old-matrix")
    public ResponseEntity<WorkerMatrixResponse> getOldWorkerMatrix(@RequestBody GetWorkerMatrixRequest request) {
        return ResponseEntity.ok(assessmentService.getOldWorkerAssessment(request));
    }

    @PostMapping("/reformat")
    public ResponseEntity<?> reformat() {
        assessmentService.rebuild();
        return ResponseEntity.ok().build();
    }

}
