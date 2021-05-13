package ru.plantarum.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.plantarum.core.entity.OperationType;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.OperationTypeRepository;
import ru.plantarum.core.repository.OrganTypeRepository;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class OperationTypeServiceTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final OperationTypeRepository repository = Mockito.mock(OperationTypeRepository.class);

    private final OperationTypeService operationTypeService =
            new OperationTypeService(repository);

    List<OperationType> createOperationTypes() {
        final OperationType operationType1 = OperationType.builder()
                .idOperationType(20L)
                .operationTypeName("bobr1")
                .build();

        final OperationType operationType2 = OperationType.builder()
                .idOperationType(30L)
                .operationTypeName("bobr2")
                .build();

        final OperationType operationType3 = OperationType.builder()
                .idOperationType(30L)
                .operationTypeName("vidra3")
                .build();

        List<OperationType> operationTypes = new ArrayList<OperationType>();
        operationTypes.add(operationType1);
        operationTypes.add(operationType2);
        operationTypes.add(operationType3);

        return operationTypes;
    }

    OperationType createOperationType() {
        final OperationType operationType = OperationType.builder().
                idOperationType(20L).
                operationTypeName("test").build();

        return operationType;
    }

    PagingRequest createRequest() throws JsonProcessingException {
        String json = "{\"draw\":21,\"columns\":[{\"data\":\"idOperationType\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"operationTypeName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"wg\",\"regexp\":false}},{\"data\":\"operationTypeComment\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}}],\"order\":[{\"column\":0,\"dir\":\"desc\"}],\"start\":0,\"length\":10,\"search\":{\"value\":\"\",\"regexp\":false}}";

        PagingRequest pagingRequest = objectMapper.readValue(json,PagingRequest.class);
        return pagingRequest;
    }

    @Test
    void find_all_if_string_to_find_is_null() throws JsonProcessingException {

        List<OperationType> operationTypes = createOperationTypes();

        org.springframework.data.domain.Page<OperationType> pagedResponse = new PageImpl<>(operationTypes);

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(null);

        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(pagedResponse);
        Page page = operationTypeService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(operationTypes));
    }

    @Test
    void find_all_if_string_to_find_is_not_null() throws JsonProcessingException {
        List<OperationType> operationTypes = createOperationTypes();

        String content = "obr";

        operationTypes.removeIf(operationType -> !operationType.getOperationTypeName().contains(content));

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(content);

        org.springframework.data.domain.Page<OperationType> pagedResponse = new PageImpl<>(operationTypes);

        Mockito.when(repository.findByOperationTypeNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(pagedResponse);
        Page page = operationTypeService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(operationTypes));
    }

    @Test
    void edit_operation_type_if_new_operation_type_equal() {
        OperationType oldOperationType = createOperationType();
        OperationType newOperationType = createOperationType();

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldOperationType);
        boolean res = operationTypeService.editOperationType(oldOperationType.getIdOperationType(), newOperationType);
        assert !res;
    }

    @Test
    void edit_operation_type_if_new_operation_type_is_not_equal() {
        OperationType oldOperationType = createOperationType();
        OperationType newOperationType = createOperationType();
        newOperationType.setOperationTypeComment("deer");

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldOperationType);
        Mockito.when(repository.save(any(OperationType.class))).thenReturn(newOperationType);
        boolean res = operationTypeService.editOperationType(oldOperationType.getIdOperationType(), newOperationType);
        assert res;
    }

    @Test
    void edit_operation_type_if_new_operation_type_name_is_busy() {
        OperationType oldOperationType = createOperationType();
        OperationType newOperationType = createOperationType();
        newOperationType.setOperationTypeName("vidra3");

        Mockito.when(repository.existsOperationTypeByOperationTypeNameIgnoreCase(any(String.class))).thenReturn(true);
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldOperationType);
        Mockito.when(repository.save(any(OperationType.class))).thenReturn(newOperationType);

        boolean res = operationTypeService.editOperationType(oldOperationType.getIdOperationType(), newOperationType);
        assert !res;
    }

    @Test
    void edit_operation_type_if_new__operation_type_name_is_free() {
        OperationType oldOperationType = createOperationType();
        OperationType newOperationType = createOperationType();
        newOperationType.setOperationTypeName("deer");

        Mockito.when(repository.existsOperationTypeByOperationTypeNameIgnoreCase(any(String.class))).thenReturn(false);
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldOperationType);
        Mockito.when(repository.save(any(OperationType.class))).thenReturn(newOperationType);

        boolean res = operationTypeService.editOperationType(oldOperationType.getIdOperationType(), newOperationType);
        assert res;
    }
}