package com.cmpeq0.neo360.worker;

import com.cmpeq0.neo360.dao.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;



}
