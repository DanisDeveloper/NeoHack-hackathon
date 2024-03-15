package com.cmpeq0.neo360.view.worker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerDataView {

    private String telegramId;
    private String position;
    private String firstName;
    private String lastName;
    private double karma;
    private String role;

}
