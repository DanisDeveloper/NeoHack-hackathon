package com.cmpeq0.neo360.view.position;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillRequirementView {

    private String name;
    private int level;
    private String priority;

}
