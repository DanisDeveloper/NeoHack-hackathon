package com.cmpeq0.neo360.service;

import com.cmpeq0.neo360.dao.SkillRecordRepository;
import com.cmpeq0.neo360.dao.SkillRepository;
import com.cmpeq0.neo360.dao.WorkerRepository;
import com.cmpeq0.neo360.model.Skill;
import com.cmpeq0.neo360.model.SkillRecord;
import com.cmpeq0.neo360.model.Worker;
import com.cmpeq0.neo360.view.skill.SkillAssignment;
import com.cmpeq0.neo360.view.skill.SkillView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillRecordRepository skillRecordRepository;
    private final WorkerRepository workerRepository;

    public void createSkill(SkillView skillView) {
        Skill skill = skillRepository.findSkillByName(skillView.getName());
        if (skill == null) {
            skill = Skill.builder().name(skillView.getName()).build();
            skillRepository.save(skill);
        }
    }

    public void deleteSkill(SkillView skillView) {
        Skill skill = skillRepository.findSkillByName(skillView.getName());
        if (skill != null) {
            skillRepository.delete(skill);
        }
    }

    public void assignSkillRecord(SkillAssignment skillAssignment) {
        Skill skill = skillRepository.findSkillByName(skillAssignment.getSkillName());
        Worker worker = workerRepository.findWorkerByTelegramId(skillAssignment.getTargetTelegramId());
        SkillRecord skillRecord = skillRecordRepository.save(
                SkillRecord.builder()
                        .skill(skill)
                        .level(skillAssignment.getLevel())
                        .build());
        worker.getPreviousSkillRecords().add(skillRecord);
        workerRepository.save(worker);
    }

    public void removeSkillRecord(SkillAssignment skillAssignment) {
        Skill skill = skillRepository.findSkillByName(skillAssignment.getSkillName());
        Worker worker = workerRepository.findWorkerByTelegramId(skillAssignment.getTargetTelegramId());
        worker.getPreviousSkillRecords().stream()
                .filter(x -> x.getSkill().getName().equals(skill.getName()))
                .findAny().ifPresent(skillRecordRepository::delete);
    }

}
