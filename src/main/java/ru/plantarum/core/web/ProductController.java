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
        List<OrganType> organTypes = organTypeService.findAll();
        List<TradeMark> tradeMarks = tradeMarkService.findAll();
        model.addAttribute("organTypes", organTypes);
        model.addAttribute("tradeMarks", tradeMarks);
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
    public String editProduct(@RequestParam Long id, @Valid Product product) {
        boolean exists = productService.exists(id);
        if(!exists){
          throw   new EntityNotFoundException(String.format("#editProductForm:  entity by id %s  not found", id) );
        }
        product.setIdProduct(id);
       //TODO check if changed -> save
        productService.save(product);
        return "redirect:/products/all";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult bindingResult) {
        if (productService.findByProductName(product.getProductName()) != null) {
            bindingResult.rejectValue("productName", "", "Уже существует");
        }
        if (bindingResult.hasErrors()) {
            //TODO does not work!!!
            return "redirect:/products/add";
        }

        productService.save(product);
        return "redirect:/products/all";
    }


}
