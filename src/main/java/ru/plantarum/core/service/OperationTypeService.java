package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.OperationType;
import ru.plantarum.core.repository.OperationTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperationTypeService {

    private final OperationTypeRepository repository;

    public List<OperationType> findAll(){
        return repository.findAll();
    }

    public OperationType findById(Long id){
        return repository.getOne(id);
    }

    public void delete(OperationType operationType){
         repository.delete(operationType);
    }

    public OperationType save (OperationType operationType){
       return repository.save(operationType);
    }
}
