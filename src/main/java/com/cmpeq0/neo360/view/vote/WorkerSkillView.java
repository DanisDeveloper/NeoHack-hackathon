package com.cmpeq0.neo360.view.vote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkerSkillView {

    private String telegramId;
    private String firstName;
    private String lastName;
    private List<String> skills;

}
