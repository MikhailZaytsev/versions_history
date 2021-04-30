package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.service.TradeMarkService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

@Controller
@RequestMapping("/trademarks")
@RequiredArgsConstructor
public class TradeMarkController {
    private final TradeMarkService tradeMarkService;

    @PostMapping
    @ResponseBody
    public Page<TradeMark> list(@RequestBody PagingRequest pagingRequest) {

        return tradeMarkService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllTradeMarks() {
        return "show-all-trade-marks";
    }
}
