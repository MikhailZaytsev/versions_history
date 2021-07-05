package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.BareCode;
import ru.plantarum.core.repository.BareCodeRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BareCodeService {
    private final BareCodeRepository bareCodeRepository;

    public BareCode save(BareCode bareCode) {
        return bareCodeRepository.save(bareCode);
    }

    public boolean exists(Long idProduct, BigDecimal ean13) {
        return bareCodeRepository.existsBareCodeByProduct_IdProductAndEan13(idProduct, ean13);
    }

    public List<BareCode> saveAll(List<BareCode> bareCodes) {
        return bareCodeRepository.saveAll(bareCodes);
    }
}
