package com.cmpeq0.neo360.controller;

import com.cmpeq0.neo360.model.Skill;
import com.cmpeq0.neo360.service.SkillService;
import com.cmpeq0.neo360.view.skill.SkillAssignment;
import com.cmpeq0.neo360.view.skill.SkillView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ap1/v1/skill")
public class SkillController {

    private final SkillService service;

    @PostMapping
    public ResponseEntity<?> createSkill(SkillView skillView) {
        service.createSkill(skillView);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteSkill(SkillView skillView) {
        service.deleteSkill(skillView);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/record")
    public ResponseEntity<?> assignRecordToWorker(SkillAssignment skillAssignment) {
        service.assignSkillRecord(skillAssignment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/record")
    public ResponseEntity<?> removeRecordFromWorker(SkillAssignment skillAssignment) {
        service.assignSkillRecord(skillAssignment);
        return ResponseEntity.ok().build();
    }

}
