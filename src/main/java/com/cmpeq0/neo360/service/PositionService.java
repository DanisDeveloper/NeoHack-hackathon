package com.cmpeq0.neo360.service;

import com.cmpeq0.neo360.dao.PositionRepository;
import com.cmpeq0.neo360.dao.SkillRepository;
import com.cmpeq0.neo360.dao.SkillRequirementRepository;
import com.cmpeq0.neo360.model.Position;
import com.cmpeq0.neo360.model.Skill;
import com.cmpeq0.neo360.model.SkillRequirement;
import com.cmpeq0.neo360.view.position.DeletePositionRequest;
import com.cmpeq0.neo360.view.position.PositionView;
import com.cmpeq0.neo360.view.position.SkillRequirementView;
import com.cmpeq0.neo360.view.skill.SkillView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;
    private final SkillRepository skillRepository;
    private final SkillRequirementRepository skillRequirementRepository;

    public void createPosition(PositionView positionView) {
        List<SkillRequirement> requirements = new ArrayList<>();
        for (SkillRequirementView skillView : positionView.getRequirements()) {
            Skill skill = skillRepository.findSkillByName(skillView.getName());
            if (skill == null) {
                skill = skillRepository.save(Skill.builder().name(skillView.getName()).build());
            }
            SkillRequirement requirement = SkillRequirement.builder()
                    .skill(skill)
                    .level(skillView.getLevel())
                    .priority(SkillRequirement.SkillPriority.valueOf(skillView.getPriority()))
                    .build();
            requirement = skillRequirementRepository.save(requirement);
            requirements.add(requirement);
        }
        positionRepository.save(Position.builder().name(positionView.getName())
                        .score(positionView.getScore())
                        .requirements(requirements).build());
    }

    private PositionView buildPositionView(Position position) {
        List<SkillRequirementView> skillRequirementViews = position.getRequirements()
                .stream().map(skillRequirement ->
                        SkillRequirementView.builder()
                                .name(skillRequirement.getSkill().getName())
                                .level(skillRequirement.getLevel())
                                .priority(skillRequirement.getPriority().toString())
                                .build()).toList();
        return PositionView.builder().name(position.getName())
                .score(position.getScore())
                .requirements(skillRequirementViews)
                .build();
    }

    public PositionView deletePosition(DeletePositionRequest request) {
        Position position = positionRepository.deletePositionByName(request.getName());
        skillRequirementRepository.deleteAll(position.getRequirements());
        return buildPositionView(position);
    }

    public List<PositionView> listPositions() {
        List<Position> positions = (List<Position>) positionRepository.findAll();
        return positions.stream().map(this::buildPositionView).toList();
    }

}
