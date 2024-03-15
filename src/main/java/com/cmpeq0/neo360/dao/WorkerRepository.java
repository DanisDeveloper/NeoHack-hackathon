package com.cmpeq0.neo360.dao;

import com.cmpeq0.neo360.model.Worker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface WorkerRepository extends CrudRepository<Worker, Long> {

    Worker findWorkerByTelegramId(String telegramId);

    Worker deleteWorkerByTelegramId(String telegramId);

}
