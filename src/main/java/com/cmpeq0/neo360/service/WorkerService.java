package com.cmpeq0.neo360.service;

import com.cmpeq0.neo360.dao.EdgeRepository;
import com.cmpeq0.neo360.dao.PositionRepository;
import com.cmpeq0.neo360.dao.WorkerRepository;
import com.cmpeq0.neo360.model.Edge;
import com.cmpeq0.neo360.model.Position;
import com.cmpeq0.neo360.model.Worker;
import com.cmpeq0.neo360.view.worker.DeleteWorkerRequest;
import com.cmpeq0.neo360.view.worker.WorkerConnectionView;
import com.cmpeq0.neo360.view.worker.WorkerDataView;
import com.cmpeq0.neo360.view.worker.WorkerView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final PositionRepository positionRepository;
    private final EdgeRepository edgeRepository;

    public void addWorker(WorkerDataView request) {
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

    public void connectWorkers(WorkerConnectionView connectionView) {
        var first = workerRepository.findWorkerByTelegramId(connectionView.getFirstTelegramId());
        var second = workerRepository.findWorkerByTelegramId(connectionView.getSecondTelegramId());
        edgeRepository.save(Edge.builder().source(first).target(second).build());
        workerRepository.save(first);
        workerRepository.save(second);
    }

    public void removeWorkerConnection(WorkerConnectionView connectionView) {
        var first = workerRepository.findWorkerByTelegramId(connectionView.getFirstTelegramId());
        var second = workerRepository.findWorkerByTelegramId(connectionView.getSecondTelegramId());
        var edge = edgeRepository.findEdgeBySourceAndTarget(first, second);
        if (edge == null) {
            edge = edgeRepository.findEdgeBySourceAndTarget(second, first);
        }
        edgeRepository.delete(edge);
        workerRepository.save(first);
        workerRepository.save(second);
    }

    public List<WorkerDataView> listWorkers() {
        List<WorkerDataView> result = new ArrayList<>();
        for (var worker : workerRepository.findAll()) {
            result.add(WorkerDataView.builder()
                    .firstName(worker.getFirstName())
                    .lastName(worker.getLastName())
                    .telegramId(worker.getTelegramId())
                    .position(worker.getPosition().getName())
                    .karma(worker.getKarma())
                    .role(worker.getRole().toString()).build());
        }
        return result;
    }

    public WorkerView getWorker(String telegramId) {
        Worker worker = workerRepository.findWorkerByTelegramId(telegramId);
        return WorkerView.builder()
                .telegramId(telegramId)
                .firstName(worker.getFirstName())
                .lastName(worker.getLastName())
                .position(worker.getPosition().getName()).build();

    }
}
