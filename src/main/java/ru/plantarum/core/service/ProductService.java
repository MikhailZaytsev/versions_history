package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public Page<Product> findAll(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest);
    }

    public Product getOne(Long id) {
        return productRepository.getOne(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product findByProductName(String productName) {
        return productRepository.findByProductName(productName);
    }

    public List<Product> findAllActive() {
        return productRepository.findByInactiveIsNull();
    }

    //TODO FIX page number calculation
//    TODO ADD sorting
    //TODO ADD filtering
    public ru.plantarum.core.web.paging.Page<Product> findAll(PagingRequest pagingRequest) {

        List<Product> products = findAllActive();
        //TODO fix 0
        int pageNumber = pagingRequest.getStart() == 0 ? 0 : products.size() / pagingRequest.getStart();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(
                order.getColumn()
        ).getData();
        ru.plantarum.core.web.paging.Page<Product> page =
                new ru.plantarum.core.web.paging.Page<>(findAll(
                        PageRequest.of(pageNumber,
                                pagingRequest.getLength(),
                                Sort.Direction.fromString(order.getDir().name()), colToOrder))
                        .toList());
        page.setRecordsFiltered(products.size());
        page.setRecordsTotal(products.size());
        page.setDraw(pagingRequest.getDraw());
        return page;
    }
}
