package ru.plantarum.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.repository.TradeMarkRepository;
import ru.plantarum.core.utils.search.CriteriaUtils;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

class TradeMarkServiceTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final TradeMarkRepository repository = Mockito.mock(TradeMarkRepository.class);
    private final CriteriaUtils criteriaUtils = Mockito.mock(CriteriaUtils.class);

    private final TradeMarkService tradeMarkService =
            new TradeMarkService(repository, criteriaUtils);

    List<TradeMark> createTradeMarks() {
        final TradeMark tradeMark1 = TradeMark.builder()
                .idTradeMark(20L)
                .tradeMarkName("bobr1")
                .build();

        final TradeMark tradeMark2 = TradeMark.builder()
                .idTradeMark(30L)
                .tradeMarkName("bobr2")
                .build();

        final TradeMark tradeMark3 = TradeMark.builder()
                .idTradeMark(30L)
                .tradeMarkName("vidra3")
                .build();

        List<TradeMark> tradeMarks = new ArrayList<TradeMark>();
        tradeMarks.add(tradeMark1);
        tradeMarks.add(tradeMark2);
        tradeMarks.add(tradeMark3);

        return tradeMarks;
    }

    TradeMark createTradeMark() {
        final TradeMark tradeMark = TradeMark.builder().
                idTradeMark(20L).
                tradeMarkName("test").build();

        return tradeMark;
    }

    PagingRequest createRequest() throws JsonProcessingException {
        String json = "{\"draw\":21,\"columns\":[{\"data\":\"idTradeMark\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"tradeMarkName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"wg\",\"regexp\":false}},{\"data\":\"tradeMarkComment\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}}],\"order\":[{\"column\":0,\"dir\":\"desc\"}],\"start\":0,\"length\":10,\"search\":{\"value\":\"\",\"regexp\":false}}";

        PagingRequest pagingRequest = objectMapper.readValue(json, PagingRequest.class);
        return pagingRequest;
    }

    @Test
    void find_all_if_string_to_find_is_null() throws JsonProcessingException {

        List<TradeMark> tradeMarks = createTradeMarks();

        org.springframework.data.domain.Page<TradeMark> pagedResponse = new PageImpl<>(tradeMarks);

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(null);

        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(pagedResponse);
        Page page = tradeMarkService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(tradeMarks));
    }

    @Test
    void find_all_if_string_to_find_is_not_null() throws JsonProcessingException {
        List<TradeMark> tradeMarks = createTradeMarks();

        String content = "obr";

        tradeMarks.removeIf(tradeMark -> !tradeMark.getTradeMarkName().contains(content));

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(content);

        org.springframework.data.domain.Page<TradeMark> pagedResponse = new PageImpl<>(tradeMarks);

        Mockito.when(repository.findByTradeMarkNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(pagedResponse);
        Page page = tradeMarkService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(tradeMarks));
    }

    @Test
    void edit_trade_mark_if_new_trade_mark_equal() {
        TradeMark oldTradeMark = createTradeMark();
        TradeMark newTradeMark = createTradeMark();

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldTradeMark);
        boolean res = tradeMarkService.editTradeMark(oldTradeMark.getIdTradeMark(), newTradeMark);
        assert !res;
    }

    @Test
    void edit_trade_mark_if_new_trade_mark_is_not_equal() {
        TradeMark oldTradeMark = createTradeMark();
        TradeMark newTradeMark = createTradeMark();
        newTradeMark.setTradeMarkComment("deer");

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldTradeMark);
        Mockito.when(repository.save(any(TradeMark.class))).thenReturn(newTradeMark);
        boolean res = tradeMarkService.editTradeMark(oldTradeMark.getIdTradeMark(), newTradeMark);
        assert res;
    }

    @Test
    void edit_trade_mark_if_new_trade_mark_name_is_busy() {
        TradeMark oldTradeMark = createTradeMark();
        TradeMark newTradeMark = createTradeMark();
        newTradeMark.setTradeMarkName("vidra3");

        Mockito.when(repository.existsTradeMarkByTradeMarkNameIgnoreCase(any(String.class))).thenReturn(true);
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldTradeMark);
        Mockito.when(repository.save(any(TradeMark.class))).thenReturn(newTradeMark);

        boolean res = tradeMarkService.editTradeMark(oldTradeMark.getIdTradeMark(), newTradeMark);
        assert !res;
    }

    @Test
    void edit_trade_mark_if_new_trade_mark_name_is_free() {
        TradeMark oldTradeMark = createTradeMark();
        TradeMark newTradeMark = createTradeMark();
        newTradeMark.setTradeMarkName("deer");

        Mockito.when(repository.existsTradeMarkByTradeMarkNameIgnoreCase(any(String.class))).thenReturn(false);
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldTradeMark);
        Mockito.when(repository.save(any(TradeMark.class))).thenReturn(newTradeMark);

        boolean res = tradeMarkService.editTradeMark(oldTradeMark.getIdTradeMark(), newTradeMark);
        assert res;
    }
}