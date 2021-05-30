package ru.plantarum.core.web;

import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.Campaign;
import ru.plantarum.core.entity.PriceBuyPreliminarily;
import ru.plantarum.core.entity.PriceSale;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.service.CampaignService;
import ru.plantarum.core.service.PriceSaleService;
import ru.plantarum.core.service.ProductService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pricesales")
public class PriceSaleController {

    private final PriceSaleService priceSaleService;
    private final CampaignService campaignService;
    private final ProductService productService;

    private List<Campaign> getCampaignList() {
        return campaignService.findAll();
    }

    private List<Product> getProductList() {
        return productService.findAll();
    }

    @ResponseBody
    @PostMapping
    public Page<PriceSale> list(@RequestBody PagingRequest pagingRequest) {
        return priceSaleService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllPriceSales() {
        return "show-all-price-sales";
    }

    @GetMapping({"/add", "/edit"})
    public String addPriceSaleForm(@RequestParam (required = false) Long id, Model model) {
        PriceSale priceSale = PriceSale.builder().build();
        if (id != null) {
            priceSale = priceSaleService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#editPriceSaleForm:  entity by id %s  not found", id)));
        }
        model.addAttribute("products", getProductList());
        model.addAttribute("campaigns", getCampaignList());
        model.addAttribute("priceSale", priceSale);
        return "add-price-sale";
    }

    @PostMapping("/add")
    public String addPriceSale(@Valid @ModelAttribute("priceSale") PriceSale priceSale, BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("products", getProductList());
            model.addAttribute("campaigns", getCampaignList());
            model.addAttribute("priceSale", priceSale);
            return "add-price-sale";
        }
        priceSaleService.save(priceSale);
        return "redirect:/pricesales/all";
     }

     @PostMapping("/edit")
    public String editPriceSale(@RequestParam Long id, @Valid @ModelAttribute("priceSale") PriceSale priceSale,
                                BindingResult bindingResult, Model model) {
        if (!priceSaleService.exists(id)) {
            throw new EntityNotFoundException(String.format("#editpriceSaleForm:  entity by id %s  not found", id));
        }

         priceSale.setIdPriceSale(id);

         if (bindingResult.hasErrors()) {
             model.addAttribute("priceSale", priceSale);
             model.addAttribute("products", getProductList());
             model.addAttribute("campaigns", getCampaignList());
             return "add-price-sale";
         }

         priceSaleService.save(priceSale);
         return "redirect:/pricesales/all";
     }
}
