package com.cmpeq0.neo360.service;

import com.cmpeq0.neo360.dao.*;
import com.cmpeq0.neo360.model.*;
import com.cmpeq0.neo360.view.assessment.GetWorkerMatrixRequest;
import com.cmpeq0.neo360.view.assessment.SkillRecordView;
import com.cmpeq0.neo360.view.assessment.WorkerMatrixResponse;
import com.cmpeq0.neo360.view.vote.SkillCalculatorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    @Getter
    private Survey currentAssessment;

    private final WorkerRepository workerRepository;
    private final AssessmentRepository assessmentRepository;
    private final SkillCalculatorService skillCalculatorService;
    private final VoteRepository voteRepository;
    private final SkillRepository skillRepository;
    private final PositionRepository positionRepository;
    private final SkillRecordRepository skillRecordRepository;
    private final SkillRequirementRepository skillRequirementRepository;
    private final EdgeRepository edgeRepository;


    public void closeCurrentAssessment() {
        if (currentAssessment == null) {
            currentAssessment = new Survey();
            currentAssessment.setStartDate(LocalDateTime.now());
            currentAssessment.setEndDate(LocalDateTime.now());
        }
        currentAssessment.setEndDate(LocalDateTime.now());
        assessmentRepository.save(currentAssessment);
        for (Worker worker : workerRepository.findAll()) {
            List<SkillRecord> nextRecords = skillCalculatorService.getCurrentSkillMatrix(currentAssessment, worker);
            voteRepository.deleteVoteByTarget(worker);
            worker.setPreviousSkillRecords(nextRecords);
        }
    }

    public Survey activateNewAssessment() {
        closeCurrentAssessment();
        currentAssessment = assessmentRepository.save(Survey.builder().startDate(LocalDateTime.now()).build());
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

    public void rebuild() {
        positionRepository.deleteAll();
        skillRepository.deleteAll();
        skillRecordRepository.deleteAll();
        skillRequirementRepository.deleteAll();
        assessmentRepository.deleteAll();
        voteRepository.deleteAll();
        workerRepository.deleteAll();

        //previous assessment
        Survey assessment = new Survey();
        assessment.setStartDate(LocalDateTime.now().minusDays(3));
        assessment.setEndDate(LocalDateTime.now().minusDays(1));
        currentAssessment = assessmentRepository.save(assessment);

        //skills
        List<String> skills = List.of("C++", "Linux", "SQL", "Qt", "Project management", "Python", "Algorithms", "Soft skills");
        skillRepository.saveAll(skills.stream().map(name -> Skill.builder().name(name).build()).toList());

        //Senior C++ position
        Position position = new Position();
        position.setName("Senior C++ developer");
        position.setScore(8);
        List<SkillRequirement> requirements = List.of(
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("C++"))
                        .level(9)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build()
                ,
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Linux"))
                        .level(9)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("SQL"))
                        .level(7)
                        .priority(SkillRequirement.SkillPriority.MEDIUM)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Qt"))
                        .level(8)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Soft skills"))
                        .level(5)
                        .priority(SkillRequirement.SkillPriority.LOW)
                        .build()
        );
        requirements = requirements.stream().map(skillRequirementRepository::save).toList();
        position.setRequirements(requirements);
        positionRepository.save(position);


        //Middle C++ developer
        position = new Position();
        position.setName("Middle C++ developer");
        position.setScore(6);
        requirements = List.of(
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("C++"))
                        .level(6)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build()
                ,
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Linux"))
                        .level(5)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("SQL"))
                        .level(5)
                        .priority(SkillRequirement.SkillPriority.MEDIUM)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Qt"))
                        .level(5)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Soft skills"))
                        .level(4)
                        .priority(SkillRequirement.SkillPriority.LOW)
                        .build()
        );
        requirements = requirements.stream().map(skillRequirementRepository::save).toList();
        position.setRequirements(requirements);
        positionRepository.save(position);


        //Junior C++ developer
        position = new Position();
        position.setName("Junior C++ developer");
        position.setScore(3);
        requirements = List.of(
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("C++"))
                        .level(4)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build()
                ,
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Linux"))
                        .level(3)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("SQL"))
                        .level(4)
                        .priority(SkillRequirement.SkillPriority.MEDIUM)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Qt"))
                        .level(4)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Soft skills"))
                        .level(4)
                        .priority(SkillRequirement.SkillPriority.LOW)
                        .build()
        );
        requirements = requirements.stream().map(skillRequirementRepository::save).toList();
        position.setRequirements(requirements);
        positionRepository.save(position);


        //Intern C++ developer
        position = new Position();
        position.setName("Intern C++ developer");
        position.setScore(2);
        requirements = List.of(
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("C++"))
                        .level(2)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build()
                ,
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Linux"))
                        .level(2)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("SQL"))
                        .level(2)
                        .priority(SkillRequirement.SkillPriority.MEDIUM)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Qt"))
                        .level(2)
                        .priority(SkillRequirement.SkillPriority.HIGH)
                        .build(),
                SkillRequirement.builder()
                        .skill(skillRepository.findSkillByName("Soft skills"))
                        .level(3)
                        .priority(SkillRequirement.SkillPriority.LOW)
                        .build()
        );
        requirements = requirements.stream().map(skillRequirementRepository::save).toList();
        position.setRequirements(requirements);
        positionRepository.save(position);



        //#############################   Danis (Senior C++)   #################################
        Worker workerDanis = new Worker();
        workerDanis.setRole(Worker.Role.USER);
        workerDanis.setFirstName("Danis");
        workerDanis.setLastName("Galimulin");
        workerDanis.setKarma(10);
        workerDanis.setTelegramId("Danis707");
        workerDanis.setPosition(positionRepository.findPositionByName("Senior C++ developer"));
        List<SkillRecord> records = List.of(
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("C++"))
                        .level(9).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Linux"))
                        .level(9).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("SQL"))
                        .level(7).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Qt"))
                        .level(8).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Soft skills"))
                        .level(5).build()
        );
        records = records.stream().map(skillRecordRepository::save).toList();
        workerDanis.setPreviousSkillRecords(records);
        workerDanis = workerRepository.save(workerDanis);


        //#############################   Ivan (Middle C++)   #################################
        Worker workerIvan = new Worker();
        workerIvan.setRole(Worker.Role.USER);
        workerIvan.setFirstName("Ivan");
        workerIvan.setLastName("Gusev");
        workerIvan.setKarma(4);
        workerIvan.setTelegramId("IvanGusev");
        workerIvan.setPosition(positionRepository.findPositionByName("Middle C++ developer"));
        records = List.of(
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("C++"))
                        .level(8).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Linux"))
                        .level(8).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("SQL"))
                        .level(8).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Qt"))
                        .level(9).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Soft skills"))
                        .level(2).build()
        );
        records = records.stream().map(skillRecordRepository::save).toList();
        workerIvan.setPreviousSkillRecords(records);
        workerIvan = workerRepository.save(workerIvan);

        //#############################   Sasha (Middle C++)   #################################
        Worker workerSasha = new Worker();
        workerSasha.setRole(Worker.Role.USER);
        workerSasha.setFirstName("Alexander");
        workerSasha.setLastName("Kovalev");
        workerSasha.setKarma(6);
        workerSasha.setTelegramId("Kisel03");
        workerSasha.setPosition(positionRepository.findPositionByName("Middle C++ developer"));
        records = List.of(
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("C++"))
                        .level(5).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Linux"))
                        .level(5).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("SQL"))
                        .level(5).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Qt"))
                        .level(5).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Soft skills"))
                        .level(7).build()
        );
        records = records.stream().map(skillRecordRepository::save).toList();
        workerSasha.setPreviousSkillRecords(records);
        workerSasha = workerRepository.save(workerSasha);

        //#############################   Misha (Junior C++)   #################################
        Worker workerMisha = new Worker();
        workerMisha.setRole(Worker.Role.USER);
        workerMisha.setFirstName("Mihail");
        workerMisha.setLastName("Novikov");
        workerMisha.setKarma(4);
        workerMisha.setTelegramId("Rizire");
        workerMisha.setPosition(positionRepository.findPositionByName("Junior C++ developer"));
        records = List.of(
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("C++"))
                        .level(4).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Linux"))
                        .level(4).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("SQL"))
                        .level(5).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Qt"))
                        .level(4).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Soft skills"))
                        .level(9).build()
        );
        records = records.stream().map(skillRecordRepository::save).toList();
        workerMisha.setPreviousSkillRecords(records);
        workerMisha = workerRepository.save(workerMisha);

        //#############################   Vlad (Intern C++)   #################################
        Worker workerVlad = new Worker();
        workerVlad.setRole(Worker.Role.USER);
        workerVlad.setFirstName("Vladislav");
        workerVlad.setLastName("Yangulov");
        workerVlad.setKarma(9);
        workerVlad.setTelegramId("Obuzzza");
        workerVlad.setPosition(positionRepository.findPositionByName("Intern C++ developer"));
        records = List.of(
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("C++"))
                        .level(2).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Linux"))
                        .level(2).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("SQL"))
                        .level(9).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Qt"))
                        .level(2).build(),
                SkillRecord.builder()
                        .survey(currentAssessment)
                        .skill(skillRepository.findSkillByName("Soft skills"))
                        .level(10).build()
        );
        records = records.stream().map(skillRecordRepository::save).toList();
        workerVlad.setPreviousSkillRecords(records);
        workerVlad = workerRepository.save(workerVlad);

        edgeRepository.save(Edge.builder().source(workerDanis).target(workerIvan).build());
        edgeRepository.save(Edge.builder().source(workerDanis).target(workerSasha).build());
        edgeRepository.save(Edge.builder().source(workerDanis).target(workerMisha).build());
        edgeRepository.save(Edge.builder().source(workerDanis).target(workerVlad).build());

        edgeRepository.save(Edge.builder().source(workerIvan).target(workerSasha).build());
        edgeRepository.save(Edge.builder().source(workerIvan).target(workerMisha).build());
        edgeRepository.save(Edge.builder().source(workerIvan).target(workerVlad).build());

        edgeRepository.save(Edge.builder().source(workerSasha).target(workerMisha).build());
        edgeRepository.save(Edge.builder().source(workerSasha).target(workerVlad).build());

        edgeRepository.save(Edge.builder().source(workerMisha).target(workerVlad).build());
    }

}
