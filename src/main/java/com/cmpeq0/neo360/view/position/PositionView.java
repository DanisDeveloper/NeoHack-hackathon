package com.cmpeq0.neo360.view.position;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PositionView {

    private String name;
    private List<SkillRequirementView> requirements;
    private double score;

}
