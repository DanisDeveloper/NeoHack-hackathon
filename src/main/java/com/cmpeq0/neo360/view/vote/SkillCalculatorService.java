package com.cmpeq0.neo360.view.vote;

import com.cmpeq0.neo360.dao.VoteRepository;
import com.cmpeq0.neo360.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SkillCalculatorService {

    private final VoteRepository voteRepository;

    private double calcColleaguesSum(Worker target, Function<Worker, Double> mapper) {
        return target.getColleagues().stream().map(mapper).reduce(0.0, Double::sum);
    }

    private double calcPositionSum(Worker target) {
        return calcColleaguesSum(target, t -> t.getPosition().getScore());
    }

    private double calcKarmaSum(Worker target) {
        return calcColleaguesSum(target, Worker::getKarma);
    }

    private double calcPersonalSkillSum(Worker target, Skill skill) {
        return (double) target.getColleagues().stream()
                .flatMap(worker -> worker.getPreviousSkillRecords().stream())
                .filter(skillRecord -> skillRecord.getSkill().getName().equals(skill.getName()))
                .map(SkillRecord::getLevel).reduce(0, Integer::sum);
    }

    public List<SkillRecord> getCurrentSkillMatrix(Assessment assessment, Worker target) {
        List<SkillRecord> nextRecords = new ArrayList<>();
        List<Vote> votes = voteRepository.findVotesByTarget(target);
        Set<Skill> used = new HashSet<>();
        double positionSum = calcPositionSum(target);
        double karmaSum = calcKarmaSum(target);
        for (var vote : votes) {
            Skill currentSkill = vote.getTargetSkill();
            if (!used.contains(currentSkill)){
                used.add(currentSkill);
                List<Vote> relevantVotes = votes.stream()
                        .filter(current -> current.getTargetSkill().equals(currentSkill)).toList();
                double sum = 0;
                double count = 0;
                double skillSum = calcPersonalSkillSum(target, currentSkill);
                for (var currentVote : relevantVotes) {
                    if (currentVote.getScore() != 0) {
                        double positionScore = vote.getSource().getPosition().getScore() / positionSum;
                        double karmaScore = vote.getSource().getKarma() / karmaSum;
                        double skillScore = vote.getSource().getSkillValue(currentSkill.getName()) / skillSum;
                        count += 1;
                        sum += (positionScore + karmaScore + skillScore) / 3 * vote.getScore();
                    }
                }
                double actualSkill = target.getSkillValue(currentSkill.getName());
                double finalScore = sum / count;
                finalScore = Math.max(actualSkill - 2, finalScore);
                finalScore = Math.min(actualSkill + 2, finalScore);
                finalScore = Math.min(10, Math.max(1, finalScore));

                nextRecords.add(SkillRecord.builder()
                        .skill(currentSkill)
                        .level((int)finalScore).assessment(assessment)
                        .build()
                );
            }
        }
        return nextRecords;
    }

}
