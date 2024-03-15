package com.cmpeq0.neo360.view.vote;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTargetsRequest {

    private String sourceTelegramId;

}
