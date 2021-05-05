package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.support.PagedListHolder;
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

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final OrganTypeService organTypeService;
    private final TradeMarkService tradeMarkService;

    private List<OrganType> getOrganTypesList() {
        return organTypeService.findAll();
    }

    private List<TradeMark> getTradeMarkList() {
       return tradeMarkService.findAll();
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
                    new EntityNotFoundException(String.format("#editProductForm:  entity by id %s  not found", id)));
        }
        model.addAttribute("product", product);
        model.addAttribute("organTypes", getOrganTypesList());
        model.addAttribute("tradeMarks", getTradeMarkList());
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
    public String editProduct(@RequestParam Long id, @Valid Product product, BindingResult bindingResult, Model model) {
        boolean exists = productService.exists(id);
        if(!exists){
          throw   new EntityNotFoundException(String.format("#editProductForm:  entity by id %s  not found", id) );
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("organTypes", getOrganTypesList());
            model.addAttribute("tradeMarks", getTradeMarkList());
            product.setIdProduct(id);
            model.addAttribute("product", product);
            return "add-product";
        }
        if (productService.editProduct(id, product)){
            return "redirect:/products/all";}
        else {
            model.addAttribute("organTypes", getOrganTypesList());
            model.addAttribute("tradeMarks", getTradeMarkList());
            return "add-product";
        }
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult,
                             Model model) {

        if (productService.findByProductName(product.getProductName()) != null) {
            bindingResult.rejectValue("productName", "", "Уже существует");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("organTypes", getOrganTypesList());
            model.addAttribute("tradeMarks", getTradeMarkList());
            return "add-product";
        }

        productService.save(product);
        return "redirect:/products/all";
    }


}
