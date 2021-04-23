package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> findContent(String content, Pageable pageable) {
       return productRepository.findByProductNameContainingIgnoreCase(content, pageable);
    }

    public List<Product> findContent(String content) {
        return productRepository.findByProductNameContainingIgnoreCase(content);
    }

    public Optional<Product> getOne(Long id) {
        return Optional.of(productRepository.getOne(id));
    }

    public boolean exists(Long id){
        return productRepository.existsById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product findByProductName(String productName) {
        return productRepository.findByProductName(productName);
    }



    //TODO ADD sorting
    //TODO ADD filtering
    public ru.plantarum.core.web.paging.Page<Product> findAll(PagingRequest pagingRequest) {

        String content = pagingRequest.getColumns().get(4).getSearch().getValue();

        if (content.isEmpty()) {
            int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
            Order order = pagingRequest.getOrder().stream()
                    .findFirst()
                    .orElse(new Order(0, Direction.desc));
            String colToOrder = pagingRequest.getColumns().get(
                    order.getColumn()
            ).getData();
            ru.plantarum.core.web.paging.Page<Product> page = new ru.plantarum.core.web.paging.Page<>(findAll(
                    PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                            order.getDir().name()), colToOrder
                    )).toList());
            List<Product> products = productRepository.findAll();

            page.setRecordsFiltered(products.size());
            page.setRecordsTotal(products.size());
            page.setDraw(pagingRequest.getDraw());
            return page;
        }
        else {
            int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
            Order order = pagingRequest.getOrder().stream()
                    .findFirst()
                    .orElse(new Order(0, Direction.desc));
            String colToOrder = pagingRequest.getColumns().get(
                    order.getColumn()
            ).getData();
            ru.plantarum.core.web.paging.Page<Product> page = new ru.plantarum.core.web.paging.Page<>(findContent(
                    content, PageRequest.of(pageNumber, pagingRequest.getLength(),
                    Sort.Direction.fromString(order.getDir().name()), colToOrder)).toList());
            List<Product> products = findContent(content);
            page.setRecordsFiltered(products.size());
            page.setRecordsTotal(products.size());
            page.setDraw(pagingRequest.getDraw());
            return page;
        }

    }
}