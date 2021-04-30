package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.service.TradeMarkService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

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

    @GetMapping({"/add", "/edit"})
    public String addTradeMarkForm(@RequestParam(required = false) Long id, Model model) {
        TradeMark tradeMark = TradeMark.builder().build();
        if (id != null) {
            tradeMark = tradeMarkService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#editProductForm:  entity by id %s  not found", id)));
        }
        model.addAttribute("tradeMark", tradeMark);
        return "add-trade-mark";
    }

    @GetMapping("/delete")
    public String deleteTradeMark(@RequestParam Long id){
        if (tradeMarkService.exists(id)) {
            tradeMarkService.save(tradeMarkService.deleteTradeMark(id));
        }
        return "redirect:/trademarks/all";
    }

    @PostMapping("/edit")
    public String editTradeMark(@RequestParam Long id, @Valid TradeMark tradeMark) {
        if (!tradeMarkService.exists(id)) {
            throw   new EntityNotFoundException(String.format("#editProductForm:  entity by id %s  not found", id));
        }
        tradeMarkService.editTradeMark(id, tradeMark);

        return "redirect:/trademarks/all";
    }

    @PostMapping("/add")
    public String addTradeMark(@Valid TradeMark tradeMark, BindingResult result) {
        if (tradeMarkService.findByTradeMarkName(tradeMark.getTradeMarkName()) != null) {
            result.rejectValue("tradeMarkName", "","Уже существует");
        }
        if (result.hasErrors()) {
            return "redirect:/trademarks/add";
        }
        tradeMarkService.save(tradeMark);
        return "redirect:/trademarks/all";
    }
}
