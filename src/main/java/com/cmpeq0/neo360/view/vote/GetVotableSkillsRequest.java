package com.cmpeq0.neo360.view.vote;

import lombok.Data;

@Data
public class GetVotableSkillsRequest {

    private String sourceTelegramId;

    private String targetTelegramId;

}
