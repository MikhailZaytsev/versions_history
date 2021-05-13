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
import ru.plantarum.core.repository.OrganTypeRepository;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class OrganTypeServiceTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final OrganTypeRepository repository = Mockito.mock(OrganTypeRepository.class);

    private final OrganTypeService organTypeService =
            new OrganTypeService(repository);

    List<OrganType> createOrganTypes() {
        final OrganType organType1 = OrganType.builder()
                .idOrganType(20L)
                .organTypeName("bobr1")
                .build();

        final OrganType organType2 = OrganType.builder()
                .idOrganType(30L)
                .organTypeName("bobr2")
                .build();

        final OrganType organType3 = OrganType.builder()
                .idOrganType(30L)
                .organTypeName("vidra3")
                .build();

        List<OrganType> organTypes = new ArrayList<OrganType>();
        organTypes.add(organType1);
        organTypes.add(organType2);
        organTypes.add(organType3);

        return organTypes;
    }

    OrganType createOrganType() {
        final OrganType organType = OrganType.builder().
                idOrganType(20L).
                organTypeName("test").build();

        return organType;
    }

    PagingRequest createRequest() throws JsonProcessingException {
        String json = "{\"draw\":21,\"columns\":[{\"data\":\"idOrganType\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"organTypeName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"wg\",\"regexp\":false}},{\"data\":\"organTypeComment\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}}],\"order\":[{\"column\":0,\"dir\":\"desc\"}],\"start\":0,\"length\":10,\"search\":{\"value\":\"\",\"regexp\":false}}";

        PagingRequest pagingRequest = objectMapper.readValue(json,PagingRequest.class);
        return pagingRequest;
    }

    @Test
    void find_all_if_string_to_find_is_null() throws JsonProcessingException {

        List<OrganType> organTypes = createOrganTypes();

        org.springframework.data.domain.Page<OrganType> pagedResponse = new PageImpl<>(organTypes);

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(null);

        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(pagedResponse);
        Page page = organTypeService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(organTypes));
    }

    @Test
    void find_all_if_string_to_find_is_not_null() throws JsonProcessingException {
        List<OrganType> organTypes = createOrganTypes();

        String content = "obr";

        organTypes.removeIf(organType -> !organType.getOrganTypeName().contains(content));

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(content);

        org.springframework.data.domain.Page<OrganType> pagedResponse = new PageImpl<>(organTypes);

        Mockito.when(repository.findByOrganTypeNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(pagedResponse);
        Page page = organTypeService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(organTypes));
    }

    @Test
    void edit_organ_type_if_new_organ_type_equal() {
        OrganType oldOrganType = createOrganType();
        OrganType newOrganType = createOrganType();

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldOrganType);
        boolean res = organTypeService.editOrganType(oldOrganType.getIdOrganType(), newOrganType);
        assert !res;
    }

    @Test
    void edit_organ_type_if_new_organ_type_is_not_equal() {
        OrganType oldOrganType = createOrganType();
        OrganType newOrganType = createOrganType();
        newOrganType.setOrganTypeComment("deer");

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldOrganType);
        Mockito.when(repository.save(any(OrganType.class))).thenReturn(newOrganType);
        boolean res = organTypeService.editOrganType(oldOrganType.getIdOrganType(), newOrganType);
        assert res;
    }

    @Test
    void edit_organ_type_if_new_organ_type_name_is_busy() {
        OrganType oldOrganType = createOrganType();
        OrganType newOrganType = createOrganType();
        newOrganType.setOrganTypeName("vidra3");

        Mockito.when(repository.existsOrganTypeByOrganTypeNameIgnoreCase(any(String.class))).thenReturn(true);
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldOrganType);
        Mockito.when(repository.save(any(OrganType.class))).thenReturn(newOrganType);

        boolean res = organTypeService.editOrganType(oldOrganType.getIdOrganType(), newOrganType);
        assert !res;
    }

    @Test
    void edit_organ_type_if_new_organ_type_name_is_free() {
        OrganType oldOrganType = createOrganType();
        OrganType newOrganType = createOrganType();
        newOrganType.setOrganTypeName("deer");

        Mockito.when(repository.existsOrganTypeByOrganTypeNameIgnoreCase(any(String.class))).thenReturn(false);
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldOrganType);
        Mockito.when(repository.save(any(OrganType.class))).thenReturn(newOrganType);

        boolean res = organTypeService.editOrganType(oldOrganType.getIdOrganType(), newOrganType);
        assert res;
    }
}