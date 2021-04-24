package ru.plantarum.core.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import java.awt.print.Pageable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

class ProductServiceTest {

    private final ProductRepository repository = Mockito.mock(ProductRepository.class);

    private final ProductService productService =
            new ProductService(repository);


    @Test
    void deleteProduct_if_inactive_is_null() {

        final Product product = Product.builder()
                .idProduct(23L)
                .build();

        Mockito.when(repository.getOne(any())).thenReturn(product);

        productService.deleteProduct(23L);
        Assertions.assertThat(product.getInactive()).isNotNull();

    }

    @Test
    void deleteProduct_if_inactive_is_not_null() {
        final OffsetDateTime now = OffsetDateTime.now();

        final Product product = Product.builder().
                idProduct(23L)
                .inactive(now)
                .build();

        Mockito.when(repository.getOne(any())).thenReturn(product);

        productService.deleteProduct(23L);

        Assertions.assertThat((product.getInactive()).isEqual(now));
    }

    @Test
    void findAll_if_stringToFind_is_null() {
        /**
         * создать 3 продукта, для помещения в список
          */
        final Product product1 = Product.builder()
                .idProduct(20L)
                .productName("bobr1")
                .build();

        final Product product2 = Product.builder()
                .idProduct(30L)
                .productName("bobr2")
                .build();

        final Product product3 = Product.builder()
                .idProduct(40L)
                .productName("vidra3")
                .build();

        List<Product> products = new ArrayList<Product>();
        products.add(product1);
        products.add(product2);
        products.add(product3);

        /**
         * создать pagingRequest?
         */
        PagingRequest pagingRequest = new PagingRequest();
        pagingRequest.getColumns().get(4).getSearch().setValue(null);

        Mockito.when(repository.findAll()).thenReturn(products);
        Page page = productService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(products));
    }
}
