package com.cmpeq0.neo360.view.vote;

import com.cmpeq0.neo360.view.worker.WorkerView;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetTargetsResponse {

    private String sourceTelegramId;

    private List<WorkerView> targets;

}
