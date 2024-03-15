package com.cmpeq0.neo360.dao;

import com.cmpeq0.neo360.model.Skill;
import com.cmpeq0.neo360.model.Vote;
import com.cmpeq0.neo360.model.Worker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VoteRepository extends CrudRepository<Vote, Long> {

    List<Vote> findVotesByTarget(Worker worker);

    List<Vote> findVoteBySourceAndTarget(Worker source, Worker target);

    Vote findVoteBySourceAndTargetAndTargetSkill(Worker source, Worker target, Skill skill);

    List<Vote> deleteVoteByTarget(Worker target);
}
