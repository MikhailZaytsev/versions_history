package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.repository.TradeMarkRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeMarkService {

    private final TradeMarkRepository tradeMarkRepository;

    public List<TradeMark> findAll() {
        return tradeMarkRepository.findAll();
    }
}
