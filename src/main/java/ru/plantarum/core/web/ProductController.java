package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.service.OrganTypeService;
import ru.plantarum.core.service.ProductService;
import ru.plantarum.core.service.TradeMarkService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final OrganTypeService organTypeService;
    private final TradeMarkService tradeMarkService;

    @GetMapping("/all")
    public String showAllProducts(Model model){
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "show-all-products";
    }

    @GetMapping("/add")
    public String addProductForm(Model model){
        List<OrganType> organTypes = organTypeService.findAll();
        List<TradeMark> tradeMarks = tradeMarkService.findAll();
        model.addAttribute("product", Product.builder().build());
        model.addAttribute("organTypes", organTypes);
        model.addAttribute("tradeMarks", tradeMarks);
        return "add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-product";
        }

        productService.save(product);
        return "redirect:/products/all";
    }

}
