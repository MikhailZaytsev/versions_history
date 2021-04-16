package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.repository.OrganTypeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganTypeService {

    private final OrganTypeRepository organTypeRepository;

    public List<OrganType> findAll() {
        return organTypeRepository.findAll();
    }
}
