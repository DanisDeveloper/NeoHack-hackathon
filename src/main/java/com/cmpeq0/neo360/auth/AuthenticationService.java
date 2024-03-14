package com.cmpeq0.neo360.auth;

import com.cmpeq0.neo360.dao.WorkerRepository;
import com.cmpeq0.neo360.model.Worker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final WorkerRepository workerRepository;

    public AuthenticationResponse authenticateRole(AuthenticationRequest request) {
        Worker worker = workerRepository.findWorkerByTelegramId(request.getTelegramId());
        Worker.Role role = Worker.Role.UNKNOWN;
        if (worker != null) {
            role = worker.getRole();
        }
        return AuthenticationResponse.builder().role(role.toString()).build();
    }

}
