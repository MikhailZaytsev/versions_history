package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.BareCode;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.service.OrganTypeService;
import ru.plantarum.core.service.ProductService;
import ru.plantarum.core.service.TradeMarkService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final OrganTypeService organTypeService;
    private final TradeMarkService tradeMarkService;

    private List<OrganType> getOrganTypesList() {
        return organTypeService.findAllActive();
    }

    private List<TradeMark> getTradeMarkList() {
       return tradeMarkService.findAllActive();
    }

    @PostMapping
    @ResponseBody
    public Page<Product> list(@RequestBody PagingRequest pagingRequest) {
        return productService.findAll(pagingRequest);
    }


    @GetMapping("/all")
    public String showAllProducts(){
        return "show-all-products";
    }

    @GetMapping({"/add", "/edit"})
    public String addProductForm(@RequestParam(required = false) Long id, Model model) {
        Product product = Product.builder().build();
        if (id != null) {
            product = productService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#product-form:  entity by id %s  not found", id)));
        }
        model.addAttribute("product", product);
        model.addAttribute("organTypes", getOrganTypesList());
        model.addAttribute("tradeMarks", getTradeMarkList());
        model.addAttribute("bareCodes", product.getBareCodes());
        return "add-product";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam Long id) {
        if (productService.exists(id)) { //проверка на существование записи с таким ID
           productService.save(productService.deleteProduct(id));
        }
        return "redirect:/products/all";
    }

    @PostMapping("/edit")
    public String editProduct(@RequestParam Long id, @Valid @RequestBody Product product,
                              BindingResult bindingResult, Model model) {
        if(!productService.exists(id)){
            throw new EntityNotFoundException(String.format("#product-form:  entity by id %s  not found", id));
        }
        product.setIdProduct(id);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors();
            model.addAttribute("product", product);
            model.addAttribute("organTypes", getOrganTypesList());
            model.addAttribute("tradeMarks", getTradeMarkList());
            return "add-product :: product-form";
        }
        final Set<BareCode> bareCodes = product.getBareCodes();
        bareCodes.forEach(bareCode -> bareCode.setProduct(product));
        productService.save(product);
        return "redirect:/products/all";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @RequestBody Product product,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors();
            model.addAttribute("product", product);
            model.addAttribute("organTypes", getOrganTypesList());
            model.addAttribute("tradeMarks", getTradeMarkList());
            return "add-product :: product-list-form";
        }
        final Set<BareCode> bareCodes = product.getBareCodes();
        bareCodes.forEach(bareCode -> bareCode.setProduct(product));
        productService.save(product);
        return "redirect:/products/all";
    }


}
