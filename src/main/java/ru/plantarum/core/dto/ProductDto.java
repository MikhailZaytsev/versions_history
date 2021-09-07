package ru.plantarum.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.service.OrganTypeService;
import ru.plantarum.core.service.TradeMarkService;

import java.time.OffsetDateTime;
import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
//@RequiredArgsConstructor
//@Builder
public class ProductDto {

//    private final OrganTypeService organTypeService;
//    private final TradeMarkService tradeMarkService;

    private Long idProduct;
    private String productName;
    private String tradeMarkName;
    private String organTypeName;
    private Short numberInPack;
    private String productComment;
    private OffsetDateTime inactive;
//    private ArrayList<String> bareCodes;

//    public Product toProduct() {
//        Product product = new Product();
//            product.setIdProduct(getIdProduct());
//            product.setProductName(getProductName());
//            product.setTradeMark(tradeMarkService.findByTradeMarkName(getTradeMarkName()));
//            product.setOrganType(organTypeService.findByOrganTypeName(getOrganTypeName()));
//            product.setNumberInPack(getNumberInPack());
//            product.setProductComment(getProductComment());
//            product.setInactive(getInactive());
//
//        return product;
//    }

    public static ProductDto fromProduct(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setIdProduct(product.getIdProduct());
        productDto.setProductName(product.getProductName());
        productDto.setTradeMarkName(product.getTradeMark().getTradeMarkName());
        productDto.setOrganTypeName(product.getOrganType().getOrganTypeName());
        productDto.setNumberInPack(product.getNumberInPack());
        productDto.setProductComment(product.getProductComment());
        productDto.setInactive(product.getInactive());

        return productDto;
    }
}
