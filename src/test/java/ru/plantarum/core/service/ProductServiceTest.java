package ru.plantarum.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.utils.search.CriteriaUtils;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

class ProductServiceTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final ProductRepository repository = Mockito.mock(ProductRepository.class);
    private final CriteriaUtils criteriaUtils = Mockito.mock(CriteriaUtils.class);

    private final ProductService productService =
            new ProductService(repository, criteriaUtils);


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

    Product createProduct() {
        OrganType organType = OrganType.builder().
                idOrganType(100L).
                organTypeName("organType").
                organTypeComment(" ").
                build();

        TradeMark tradeMark = TradeMark.builder().
                idTradeMark(200L).
                tradeMarkName("tradeMark").
                tradeMarkComment(" ").
                build();

        final Product product = Product.builder().
                idProduct(10L).
                productName("test").
                numberInPack((short)2).
                tradeMark(tradeMark).
                organType(organType).
                productComment(" ").
                build();

        return product;
    }

    PagingRequest createRequest() throws JsonProcessingException {
        String json = "{\"draw\":21,\"columns\":[{\"data\":\"idProduct\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"tradeMark.tradeMarkName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"organType.organTypeName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"numberInPack\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"productName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"wg\",\"regexp\":false}},{\"data\":\"productComment\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"inactive\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}}],\"order\":[{\"column\":0,\"dir\":\"desc\"}],\"start\":0,\"length\":10,\"search\":{\"value\":\"\",\"regexp\":false}}";

        PagingRequest pagingRequest = objectMapper.readValue(json,PagingRequest.class);
        return pagingRequest;
    }


    @Test
    void delete_product_if_inactive_is_null() {

        final Product product = Product.builder()
                .idProduct(23L)
                .build();

        Mockito.when(repository.getOne(any())).thenReturn(product);

        productService.deleteProduct(23L);
        Assertions.assertThat(product.getInactive()).isNotNull();

    }

    @Test
    void delete_product_if_inactive_is_not_null() {
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
    void find_all_if_string_to_find_is_null() throws JsonProcessingException {

        List<Product> products = createProducts();

        org.springframework.data.domain.Page<Product> pagedResponse = new PageImpl<>(products);

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(null);

        //Mockito.when(repository.findAll()).thenReturn(pagedResponse);
        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(pagedResponse);
        Page page = productService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(products));
    }

    @Test
    void findAll_if_string_to_find_is_not_null() throws JsonProcessingException {
        List<Product> products = createProducts();

        String content = "obr";

        products.removeIf(product -> !product.getProductName().contains(content));

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(content);

        org.springframework.data.domain.Page<Product> pagedResponse = new PageImpl<>(products);

        Mockito.when(repository.findByProductNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(pagedResponse);
        Page page = productService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(products));
    }

    @Test
    void edit_product_if_new_product_equal() {
        Product oldProduct = createProduct();
        Product newProduct = createProduct();

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldProduct);
        boolean res = productService.editProduct(oldProduct.getIdProduct(), newProduct);
        assert !res;
    }

    @Test
    void edit_product_if_product_name_is_not_equal() {
        Product oldProduct = createProduct();
        Product newProduct = createProduct();
        newProduct.setProductComment("deer");

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldProduct);
        Mockito.when(repository.save(any(Product.class))).thenReturn(newProduct);
        boolean res = productService.editProduct(oldProduct.getIdProduct(), newProduct);
        assert res;
    }

    @Test
    void edit_product_if_new_product_product_name_is_busy() {
        Product oldProduct = createProduct();
        Product newProduct = createProduct();

        newProduct.setProductName("vidra3");
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldProduct);
        Mockito.when(repository.save(any(Product.class))).thenReturn(newProduct);
        Mockito.when(repository.existsProductByProductNameIgnoreCase(any(String.class))).thenReturn(true);

        boolean res = productService.editProduct(oldProduct.getIdProduct(), newProduct);
        assert !res;
    }

    @Test
    void edit_product_if_new_product_product_name_is_free() {
        Product oldProduct = createProduct();
        Product newProduct = createProduct();

        newProduct.setProductName("deer");
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldProduct);
        Mockito.when(repository.save(any(Product.class))).thenReturn(newProduct);
        Mockito.when(repository.existsProductByProductNameIgnoreCase(any(String.class))).thenReturn(false);

        boolean res = productService.editProduct(oldProduct.getIdProduct(), newProduct);
        assert res;
    }
}
