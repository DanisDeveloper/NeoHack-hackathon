package com.cmpeq0.neo360.view.worker;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerView {

    private String telegramId;
    private String firstName;
    private String lastName;
    private String position;

}
