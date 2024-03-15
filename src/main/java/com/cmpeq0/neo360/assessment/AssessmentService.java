package com.cmpeq0.neo360.assessment;

import com.cmpeq0.neo360.dao.AssessmentRepository;
import com.cmpeq0.neo360.dao.VoteRepository;
import com.cmpeq0.neo360.dao.WorkerRepository;
import com.cmpeq0.neo360.model.*;
import com.cmpeq0.neo360.vote.SkillCalculatorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    @Getter
    private Assessment currentAssessment;

    private final WorkerRepository workerRepository;
    private final AssessmentRepository assessmentRepository;
    private final SkillCalculatorService skillCalculatorService;
    private final VoteRepository voteRepository;

    public void closeCurrentAssessment() {
        currentAssessment.setEnd(LocalDateTime.now());
        assessmentRepository.save(currentAssessment);
        for (Worker worker : workerRepository.findAll()) {
            List<SkillRecord> nextRecords = skillCalculatorService.getCurrentSkillMatrix(currentAssessment, worker);
            voteRepository.deleteVoteByTarget(worker);
            worker.setPreviousSkillRecords(nextRecords);
        }
    }

    public Assessment activateNewAssessment() {
        closeCurrentAssessment();
        currentAssessment = assessmentRepository.save(Assessment.builder().start(LocalDateTime.now()).build());
        return currentAssessment;
    }

    private static WorkerMatrixResponse buildResponse(String telegramId, Position position, List<SkillRecord> records) {
        Map<String, Integer> requirements = new HashMap<>();
        position.getRequirements().forEach(r -> requirements.put(r.getSkill().getName(), r.getLevel()));
        List<SkillRecordView> recordViews = new ArrayList<>();
        for (var record : records) {
            int level = record.getLevel();
            Integer requirement = requirements.get(record.getSkill().getName());
            String rating;
            if (requirement == null) {
                rating = "satisfactory";
            } else if (level < requirement) {
                rating = "unsatisfactory";
            } else if (level == requirement) {
                rating = "satisfactory";
            } else {
                rating = "above requirements";
            }

            SkillRecordView currentView = SkillRecordView.builder()
                    .name(record.getSkill().getName())
                    .level(record.getLevel())
                    .rating(rating).build();
            recordViews.add(currentView);
        }
        return WorkerMatrixResponse.builder()
                .telegramId(telegramId)
                .skills(recordViews)
                .build();
    }

    public WorkerMatrixResponse assessWorker(GetWorkerMatrixRequest request) {
        Worker worker = workerRepository.findWorkerByTelegramId(request.getTelegramId());
        List<SkillRecord> records = skillCalculatorService.getCurrentSkillMatrix(currentAssessment, worker);
        return buildResponse(worker.getTelegramId(), worker.getPosition(), records);
    }

    public WorkerMatrixResponse getOldWorkerAssessment(GetWorkerMatrixRequest request) {
        Worker worker = workerRepository.findWorkerByTelegramId(request.getTelegramId());
        List<SkillRecord> records = worker.getPreviousSkillRecords();
        return buildResponse(worker.getTelegramId(), worker.getPosition(), records);
    }

}
