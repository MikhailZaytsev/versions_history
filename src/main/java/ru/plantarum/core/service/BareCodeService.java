package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.BareCode;
import ru.plantarum.core.repository.BareCodeRepository;

@Service
@RequiredArgsConstructor
public class BareCodeService {
    private final BareCodeRepository bareCodeRepository;

    public BareCode save(BareCode bareCode) {
        return bareCodeRepository.save(bareCode);
    }
}
