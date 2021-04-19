package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.service.OrganTypeService;
import ru.plantarum.core.service.ProductService;
import ru.plantarum.core.service.TradeMarkService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final OrganTypeService organTypeService;
    private final TradeMarkService tradeMarkService;


    @PostMapping
    @ResponseBody
    public Page<Product> list(@RequestBody PagingRequest pagingRequest) {

        return productService.findAll(pagingRequest);
    }


    @GetMapping("/all")
    public String showAllProducts(HttpServletRequest request, Model model){
        return "show-all-products";
    }

    @GetMapping("/add")
    public String addProductForm(Model model){
        model.addAttribute("product", Product.builder().build());
        List<OrganType> organTypes = organTypeService.findAll();
        List<TradeMark> tradeMarks = tradeMarkService.findAll();
        model.addAttribute("organTypes", organTypes);
        model.addAttribute("tradeMarks", tradeMarks);
        return "add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult bindingResult) {
        if (productService.findByProductName(product.getProductName()) != null) {
            bindingResult.rejectValue("productName", "", "Уже существует");
        }
        if (bindingResult.hasErrors()) {
            return "redirect:/products/add";
        }

        productService.save(product);
        return "redirect:/products/all";
    }

}
