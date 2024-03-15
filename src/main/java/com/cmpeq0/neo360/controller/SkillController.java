package com.cmpeq0.neo360.controller;

import com.cmpeq0.neo360.service.SkillService;
import com.cmpeq0.neo360.view.skill.SkillAssignment;
import com.cmpeq0.neo360.view.skill.SkillView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ap1/v1/skill")
public class SkillController {

    private final SkillService service;

    @GetMapping
    public ResponseEntity<List<SkillView>> listSkills() {
        return ResponseEntity.ok(service.listSkills());
    }

    @PostMapping
    public ResponseEntity<?> createSkill(@RequestBody SkillView skillView) {
        service.createSkill(skillView);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteSkill(@RequestBody SkillView skillView) {
        service.deleteSkill(skillView);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/record")
    public ResponseEntity<?> assignRecordToWorker(@RequestBody SkillAssignment skillAssignment) {
        service.assignSkillRecord(skillAssignment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/record")
    public ResponseEntity<?> removeRecordFromWorker(@RequestBody SkillAssignment skillAssignment) {
        service.removeSkillRecord(skillAssignment);
        return ResponseEntity.ok().build();
    }

}
