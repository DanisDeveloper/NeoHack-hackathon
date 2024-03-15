package com.cmpeq0.neo360.controller;

import com.cmpeq0.neo360.service.VoteService;
import com.cmpeq0.neo360.view.vote.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vote")
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/targets")
    public ResponseEntity<GetTargetsResponse> getVoteTargets(@RequestBody GetTargetsRequest request) {
        return ResponseEntity.ok(voteService.getTargets(request));
    }

    @PostMapping("/votable")
    public ResponseEntity<WorkerSkillView> getVotableSkills(@RequestBody GetVotableSkillsRequest request) {
        return ResponseEntity.ok(voteService.getVotableSkills(request));
    }

    @PostMapping
    public ResponseEntity<?> createVote(@RequestBody CreateVoteRequest request) {
        voteService.createVote(request);
        return ResponseEntity.ok().build();
    }




}
