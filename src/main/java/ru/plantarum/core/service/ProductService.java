package ru.plantarum.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.plantarum.core.dto.ProductDto;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.utils.search.CriteriaUtils;
import ru.plantarum.core.utils.search.SearchCriteria;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final CriteriaUtils criteriaUtils;

    public List<Product> findAll() {
        return productRepository.findAll();
    }


    public boolean editProduct(Long id, Product newProduct) {
        Product product = productRepository.getOne(id);
        newProduct.setIdProduct(id);
        if (!product.equals(newProduct)) {
            if (product.getProductName().equalsIgnoreCase(newProduct.getProductName())) {
                productRepository.save(newProduct);
                return true;
            } else if (!exists(newProduct.getProductName())) {
                productRepository.save(newProduct);
                return true;
            }
        }
        return false;
    }


    public Optional<Product> getOne(Long id) {
        return Optional.of(productRepository.getOne(id));
    }

    public boolean exists(Product product) {
        if (product.getNumberInPack() == null) {
            return productRepository.existsProductByProductNameIgnoreCaseAndTradeMark_IdTradeMarkAndNumberInPackIsNull(
                    product.getProductName(), product.getTradeMark().getIdTradeMark()
            );
        } else {
            return productRepository.existsProductByProductNameIgnoreCaseAndTradeMark_IdTradeMarkAndNumberInPack(
                    product.getProductName(), product.getTradeMark().getIdTradeMark(), product.getNumberInPack()
            );
        }
    }

    public boolean exists(String name) {
        return productRepository.existsProductByProductNameIgnoreCase(name);
    }

    public boolean exists(String name, Long idTradeMark, short numberInPack) {
        return productRepository.existsProductByProductNameIgnoreCaseAndTradeMark_IdTradeMarkAndNumberInPack(name, idTradeMark, numberInPack);
    }

    public boolean exists(String name, Long idTradeMark) {
        return productRepository.existsProductByProductNameIgnoreCaseAndTradeMark_IdTradeMarkAndNumberInPackIsNull(name, idTradeMark);
    }

    public boolean exists(Long id) {
        return productRepository.existsById(id);
    }

    public Product deleteProduct(Long id) {
        Product product = productRepository.getOne(id);
        if (product.getInactive() == null) {
            OffsetDateTime dateTime = OffsetDateTime.now();
            product.setInactive(dateTime);
        }
        return product;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> saveAll(List<Product> products) {
        return productRepository.saveAll(products);
    }

    public List<Product> findByProductName(String productName) {
        return productRepository.findByProductNameIgnoreCase(productName);
    }

    public Product findProduct(Product product) {
        if (product.getNumberInPack() == null) {
            return productRepository.findByProductNameIgnoreCaseAndTradeMark_IdTradeMarkAndNumberInPackIsNull(
                    product.getProductName(), product.getTradeMark().getIdTradeMark()
            );
        } else {
            return productRepository.findByProductNameIgnoreCaseAndTradeMark_IdTradeMarkAndNumberInPack(
                    product.getProductName(), product.getTradeMark().getIdTradeMark(), product.getNumberInPack()
            );
        }
    }

    public Product findProduct(String name, Long idTradeMark, short numberInPack) {
        return productRepository.findByProductNameIgnoreCaseAndTradeMark_IdTradeMarkAndNumberInPack(name, idTradeMark, numberInPack);
    }

    public Product findProduct(String name, Long idTradeMark) {
        return productRepository.findByProductNameIgnoreCaseAndTradeMark_IdTradeMarkAndNumberInPackIsNull(name, idTradeMark);
    }

    public ru.plantarum.core.web.paging.Page<Product> findAll(PagingRequest pagingRequest) {
        final List<SearchCriteria> criteriaList = pagingRequest.getColumns()
                .stream().filter(c -> !(c.getSearch().getValue().isEmpty()))
                .map(column -> new SearchCriteria(column.getData(),
                        SearchCriteria.OPERATION_EQUALS, column.getSearch().getValue())
                ).collect(Collectors.toList());

        final Predicate predicates = criteriaUtils.getPredicate(criteriaList,
                Product.class, "product");

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        Page<Product> filteredProducts = productRepository.findAll(predicates, pageRequest);
        List<ProductDto> pr = getProductDto(filteredProducts.getContent());
        final Page<ProductDto> productPage = new PageImpl<ProductDto>(pr, filteredProducts.getPageable(), filteredProducts.getTotalElements());
        ru.plantarum.core.web.paging.Page<Product> page = new ru.plantarum.core.web.paging.Page(productPage);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

    private List<ProductDto> getProductDto(List<Product> products) {
        ArrayList<ProductDto> pr = new ArrayList<>();
        for (Product product : products) {
            pr.add(ProductDto.fromProduct(product));
        }
        return pr;
    }
}