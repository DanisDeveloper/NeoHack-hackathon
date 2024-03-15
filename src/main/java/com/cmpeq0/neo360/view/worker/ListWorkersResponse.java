package com.cmpeq0.neo360.view.worker;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListWorkersResponse {

    private List<WorkerView> workers;

}
