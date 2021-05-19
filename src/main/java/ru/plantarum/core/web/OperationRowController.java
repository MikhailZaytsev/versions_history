package ru.plantarum.core.web;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.OperationList;
import ru.plantarum.core.entity.OperationRow;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.service.OperationListService;
import ru.plantarum.core.service.OperationRowService;
import ru.plantarum.core.service.ProductService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/operationrows")
public class OperationRowController {

    private final OperationListService operationListService;
    private final OperationRowService operationRowService;
    private final ProductService productService;

    private List<Product> getProductsList() {
        return productService.findAll();
    }

    private OperationList operationList = OperationList.builder().build();

//    @GetMapping("/add/{operationListId}")
//    public String addOperationRow(Model model){
//        Long id = 20L;
//        OperationList operationList = operationListService.getOne(id).orElseThrow(() ->
//                new EntityNotFoundException(String.format("#editOperationListForm:  entity by id %s  not found", id)));
//        model.addAttribute("operationList", operationList);
//        return "add-operation-row";
//    }

    //    @PostMapping("/add/{operationListId}")
    @GetMapping("/add")
    public String addRows(@RequestParam(required = false) Long id, Model model) {
        operationList = operationListService.getOne(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("#editOperationListForm:  entity by id %s  not found", id)));
        model.addAttribute("operationList", operationList);
        model.addAttribute("operationRows", operationList.getOperationRows());
        model.addAttribute("products", getProductsList());
        return "add-operation-row";
    }

    @PostMapping("/row")
    public String addOperationRow(@RequestParam("quantity") short quantity,
                                  @RequestParam("operationPrice") BigDecimal operationPrice,
                                  @RequestParam("product") Product product, Model model) {
        OperationRow operationRow = OperationRow.builder()
                .operationList(operationList)
                .quantity(quantity)
                .product(product)
                .operationPrice(operationPrice)
                .build();

        operationList.getOperationRows().add(operationRow);

        model.addAttribute("operationList", operationList);
        model.addAttribute("operationRows", operationList.getOperationRows());
        model.addAttribute("products", getProductsList());
        return "add-operation-row";
//        return "redirect:/operationrows/add?id=" + operationList.getIdOperationList();
    }

}
