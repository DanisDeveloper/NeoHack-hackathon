package com.cmpeq0.neo360.vote;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vote")
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/targets")
    public ResponseEntity<GetTargetsResponse> getVoteTargets(GetTargetsRequest request) {
        return ResponseEntity.ok(voteService.getTargets(request));
    }

    @GetMapping("/votable")
    public ResponseEntity<WorkerSkillView> getVotableSkills(GetVotableSkillsRequest request) {
        return ResponseEntity.ok(voteService.getVotableSkills(request));
    }

    @PostMapping
    public ResponseEntity<?> createVote(CreateVoteRequest request) {
        voteService.createVote(request);
        return ResponseEntity.ok().build();
    }




}
