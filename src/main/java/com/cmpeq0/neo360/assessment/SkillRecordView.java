package com.cmpeq0.neo360.assessment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillRecordView {

    private String name;

    private int level;

    private String rating;

}
