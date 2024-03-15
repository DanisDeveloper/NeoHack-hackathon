package com.cmpeq0.neo360.vote;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetTargetsRequest {

    private String sourceTelegramId;

}
