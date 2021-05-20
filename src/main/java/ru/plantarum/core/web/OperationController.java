package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.plantarum.core.entity.OperationList;
import ru.plantarum.core.entity.OperationRow;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.service.OperationListService;
import ru.plantarum.core.service.OperationRowService;
import ru.plantarum.core.service.ProductService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class OperationController {
    private final OperationRowService operationRowService;
    private final OperationListService operationListService;
    private final ProductService productService;

    public List<OperationList> getLists() {
        return operationListService.allLists();
    }

    private List<Product> getProductsList() {
        return productService.findAll();
    }

    @GetMapping("/add")
    public String addOperationForm(Model model) {
        OperationRow operationRow = OperationRow.builder().build();
        model.addAttribute("operationRow", operationRow);
        model.addAttribute("operationLists", getLists());
        model.addAttribute("products", getProductsList());
        return "test-operation";
    }

    @PostMapping("/add")
    public String addRow(@Valid @ModelAttribute("operationRow") OperationRow operationRow,
                         BindingResult bindingResult, Model model) {
        operationRowService.save(operationRow);
        return "redirect:/test/add";
    }
}
