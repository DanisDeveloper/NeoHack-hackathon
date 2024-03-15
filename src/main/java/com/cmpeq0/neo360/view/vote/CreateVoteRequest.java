package com.cmpeq0.neo360.view.vote;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateVoteRequest {

    private String sourceTelegramId;
    private String targetTelegramId;
    private String skillName;
    private int score;

}
