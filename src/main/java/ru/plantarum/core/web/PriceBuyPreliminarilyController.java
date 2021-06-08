package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.Campaign;
import ru.plantarum.core.entity.CounterAgent;
import ru.plantarum.core.entity.PriceBuyPreliminarily;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.service.CampaignService;
import ru.plantarum.core.service.CounterAgentService;
import ru.plantarum.core.service.PriceBuyPreliminarilyService;
import ru.plantarum.core.service.ProductService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pricebuys")
public class PriceBuyPreliminarilyController {

    private final PriceBuyPreliminarilyService priceBuyPreliminarilyService;
    private final CampaignService campaignService;
    private final ProductService productService;
    private final CounterAgentService counterAgentService;


    private List<Campaign> getCampaignList() {
        return campaignService.findAll();
    }

    private List<Product> getProductList() {
        return productService.findAll();
    }

    private List<CounterAgent> getCounterAgentList() {
        return counterAgentService.findAll();
    }

    @ResponseBody
    @PostMapping
    public Page<PriceBuyPreliminarily> list(@RequestBody PagingRequest pagingRequest) {
        return priceBuyPreliminarilyService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllPriceBuys() {
        return "show-all-price-buy-preliminarily";
    }

    @GetMapping({"/add", "/edit"})
    public String addPriceBuyFrom(@RequestParam (required = false) Long id, Model model) {
        PriceBuyPreliminarily priceBuyPreliminarily = PriceBuyPreliminarily.builder().build();
        if (id != null) {
            priceBuyPreliminarily = priceBuyPreliminarilyService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#editPriceBuyForm:  entity by id %s  not found", id)));
        }
        model.addAttribute("priceBuyPreliminarily", priceBuyPreliminarily);
        model.addAttribute("products", getProductList());
        model.addAttribute("counterAgents", getCounterAgentList());
        model.addAttribute("campaigns", getCampaignList());
        return "add-price-buy-preliminarily";
    }

    @PostMapping("/edit")
    public String editPriceBuy(@RequestParam Long id, @Valid @ModelAttribute("priceBuyPreliminarily") PriceBuyPreliminarily priceBuyPreliminarily,
                               BindingResult bindingResult, Model model) {
        if (!priceBuyPreliminarilyService.exists(id)) {
            throw new EntityNotFoundException(String.format("#editpriceBuyForm:  entity by id %s  not found", id));
        }
        priceBuyPreliminarily.setIdPriceBuy(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("priceBuyPreliminarily", priceBuyPreliminarily);
            model.addAttribute("products", getProductList());
            model.addAttribute("counterAgents", getCounterAgentList());
            model.addAttribute("campaigns", getCampaignList());
            return "add-price-buy-preliminarily";
        }

        priceBuyPreliminarilyService.save(priceBuyPreliminarily);
        return "redirect:/pricebuys/all";
    }

    @PostMapping("/add")
    public String addPriceBuy(@Valid @ModelAttribute("priceBuyPreliminarily") PriceBuyPreliminarily priceBuyPreliminarily,
                              BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("priceBuyPreliminarily", priceBuyPreliminarily);
            model.addAttribute("products", getProductList());
            model.addAttribute("counterAgents", getCounterAgentList());
            model.addAttribute("campaigns", getCampaignList());
            return "add-price-buy-preliminarily";
        }
        priceBuyPreliminarilyService.save(priceBuyPreliminarily);
        return "redirect:/pricebuys/all";
    }
}
