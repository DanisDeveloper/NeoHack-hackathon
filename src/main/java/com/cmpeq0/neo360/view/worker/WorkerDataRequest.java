package com.cmpeq0.neo360.view.worker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDataRequest {

    private String telegramId;
    private String position;
    private String firstName;
    private String lastName;
    private double karma;
    private String role;

}
