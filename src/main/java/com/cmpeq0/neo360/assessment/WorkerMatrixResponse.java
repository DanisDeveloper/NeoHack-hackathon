package com.cmpeq0.neo360.assessment;

import com.cmpeq0.neo360.model.SkillRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkerMatrixResponse {

    private String telegramId;

    private List<SkillRecordView> skills;

}
