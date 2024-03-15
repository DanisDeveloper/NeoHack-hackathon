package com.cmpeq0.neo360.controller;

import com.cmpeq0.neo360.service.AssessmentService;
import com.cmpeq0.neo360.view.assessment.GetWorkerMatrixRequest;
import com.cmpeq0.neo360.view.assessment.WorkerMatrixResponse;
import com.cmpeq0.neo360.model.Assessment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping("/start")
    public ResponseEntity<Assessment> startAssessment() {
        return ResponseEntity.ok(assessmentService.activateNewAssessment());
    }

    @GetMapping("/matrix")
    public ResponseEntity<WorkerMatrixResponse> getWorkerMatrix(GetWorkerMatrixRequest request) {
        return ResponseEntity.ok(assessmentService.assessWorker(request));
    }

    @GetMapping("/old-matrix")
    public ResponseEntity<WorkerMatrixResponse> getOldWorkerMatrix(GetWorkerMatrixRequest request) {
        return ResponseEntity.ok(assessmentService.getOldWorkerAssessment(request));
    }

}
