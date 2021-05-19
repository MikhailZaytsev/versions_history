package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.OperationRow;
import ru.plantarum.core.repository.OperationRowRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationRowService {
    OperationRowRepository operationRowRepository;

    public List<OperationRow> saveAll(List<OperationRow> operationRowList) {
       return operationRowRepository.saveAll(operationRowList);
    }

    public OperationRow save(OperationRow operationRow) {
        return operationRowRepository.save(operationRow);
    }
}
