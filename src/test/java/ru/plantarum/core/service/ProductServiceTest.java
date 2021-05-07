package ru.plantarum.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.web.paging.Column;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;
import ru.plantarum.core.web.paging.Search;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

class ProductServiceTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final ProductRepository repository = Mockito.mock(ProductRepository.class);

    private final ProductService productService =
            new ProductService(repository);

    List<Product> createProducts() {
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

        return products;
    }


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
    void findAll_if_stringToFind_is_null() throws JsonProcessingException {
        /**
         * создать 3 продукта, для помещения в список
          */
        List<Product> products = createProducts();

        org.springframework.data.domain.Page<Product> pagedResponse = new PageImpl<>(products);

        /**
         * создать pagingRequest?
         */
        String json = "{\"draw\":21,\"columns\":[{\"data\":\"idProduct\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"tradeMark.tradeMarkName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"organType.organTypeName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"numberInPack\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"productName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"wg\",\"regexp\":false}},{\"data\":\"productComment\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"inactive\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}}],\"order\":[{\"column\":0,\"dir\":\"desc\"}],\"start\":0,\"length\":10,\"search\":{\"value\":\"\",\"regexp\":false}}";

        PagingRequest pagingRequest = objectMapper.readValue(json,PagingRequest.class);
        pagingRequest.getColumns().get(1).getSearch().setValue(null);

        //Mockito.when(repository.findAll()).thenReturn(pagedResponse);
        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(pagedResponse);
        Page page = productService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(products));
    }
}
