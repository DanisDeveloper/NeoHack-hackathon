package com.cmpeq0.neo360.worker;

import com.cmpeq0.neo360.dao.PositionRepository;
import com.cmpeq0.neo360.dao.WorkerRepository;
import com.cmpeq0.neo360.model.Position;
import com.cmpeq0.neo360.model.Worker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final PositionRepository positionRepository;

    public void addWorker(WorkerDataRequest request) {
        Position position = positionRepository.findPositionByName(request.getPosition());
        if (position == null) {
            throw new IllegalArgumentException();
        }
        Worker worker = Worker.builder()
                .telegramId(request.getTelegramId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .position(position)
                .role(Worker.Role.valueOf(request.getRole()))
                .build();
        workerRepository.save(worker);
    }

    public WorkerView deleteWorker(DeleteWorkerRequest request) {
        Worker worker = workerRepository.deleteWorkerByTelegramId(request.getTelegramId());
        if (worker == null) {
            throw new IllegalArgumentException();
        }
        return WorkerView.builder()
                .telegramId(worker.getTelegramId())
                .firstName(worker.getFirstName())
                .lastName(worker.getLastName())
                .position(worker.getPosition().getName())
                .build();
    }
}
