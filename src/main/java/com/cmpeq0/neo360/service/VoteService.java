package com.cmpeq0.neo360.service;

import com.cmpeq0.neo360.dao.EdgeRepository;
import com.cmpeq0.neo360.dao.SkillRepository;
import com.cmpeq0.neo360.dao.VoteRepository;
import com.cmpeq0.neo360.dao.WorkerRepository;
import com.cmpeq0.neo360.model.*;
import com.cmpeq0.neo360.view.vote.*;
import com.cmpeq0.neo360.view.worker.WorkerView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final WorkerRepository workerRepository;
    private final SkillRepository skillRepository;
    private final EdgeService edgeService;

    public GetTargetsResponse getTargets(GetTargetsRequest request) {
        Worker source = workerRepository.findWorkerByTelegramId(request.getSourceTelegramId());
        List<WorkerView> targets  = edgeService.findAllTargets(source).stream()
                .filter(t -> {
                    List<Vote> relevantVotes = voteRepository.findVoteBySourceAndTarget(source, t);
                    int count = t.getPosition().getRequirements().size() - relevantVotes.size();
                    return count != 0;
                })
                .map(worker -> WorkerView.builder()
                        .telegramId(worker.getTelegramId())
                        .firstName(worker.getFirstName())
                        .lastName(worker.getLastName())
                        .position(worker.getPosition().getName())
                        .build()
                ).toList();
        return GetTargetsResponse.builder().sourceTelegramId(source.getTelegramId()).targets(targets).build();
    }

    public WorkerSkillView getVotableSkills(GetVotableSkillsRequest request) {
        Worker source = workerRepository.findWorkerByTelegramId(request.getSourceTelegramId());
        Worker target = workerRepository.findWorkerByTelegramId(request.getTargetTelegramId());
        List<String> skills = target.getPosition().getRequirements().stream()
                .map(SkillRequirement::getSkill)
                .filter(s -> voteRepository.findVoteBySourceAndTargetAndTargetSkill(source, target, s) == null)
                .map(Skill::getName)
                .toList();

        return WorkerSkillView.builder()
                .telegramId(request.getTargetTelegramId())
                .firstName(target.getFirstName())
                .lastName(target.getLastName())
                .skills(skills)
                .build();
    }

    public void createVote(CreateVoteRequest request) {
        Worker source = workerRepository.findWorkerByTelegramId(request.getSourceTelegramId());
        Worker target = workerRepository.findWorkerByTelegramId(request.getTargetTelegramId());
        Skill skill = skillRepository.findSkillByName(request.getSkillName());
        if (skill == null) {
            skill = Skill.builder().name(request.getSkillName()).build();
            skill = skillRepository.save(skill);
        }
        voteRepository.save(Vote.builder()
                .source(source)
                .target(target)
                .targetSkill(skill)
                .score(request.getScore())
                .build());
    }


}
