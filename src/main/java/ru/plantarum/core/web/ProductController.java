package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.service.OrganTypeService;
import ru.plantarum.core.service.ProductService;
import ru.plantarum.core.service.TradeMarkService;

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

    @GetMapping("/all")
    public String showAllProducts(HttpServletRequest request, Model model){

        int page = 0;
        int size = 10;

        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            page = Integer.parseInt(request.getParameter("page")) - 1;
        }

        if (request.getParameter("size") != null && !request.getParameter("size").isEmpty()) {
            size = Integer.parseInt(request.getParameter("size"));
        }

        model.addAttribute("products", productService.findAll(PageRequest.of(page, size)));
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
