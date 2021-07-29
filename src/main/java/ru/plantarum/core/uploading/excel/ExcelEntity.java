package ru.plantarum.core.uploading.excel;

import lombok.*;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.uploading.response.InvalidParse;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@ToString
public class ExcelEntity {

    //fields from frontend
    private String tempFileName;
    private List<String> headers;
    private Campaign campaign;
    private TradeMark tradeMark;
    private OrganType organType;
    private CounterAgent counterAgent;

    //collections for save entities
    private Map<Integer, Product> products = new HashMap<>();
    private Map<Integer, BareCode> bareCodes = new HashMap<>();
    private Map<Integer, PriceBuyPreliminarily> priceBuyPreliminarilyMap = new HashMap<>();
    private Map<Integer, PriceSale> priceSales = new HashMap<>();
    //collection products from DB
    private Map<Integer, Product> existsProducts = new HashMap<>();

    //collections for messages
    private Map<Integer, List<InvalidParse>> errors = new HashMap<>();
    private Map<Integer, List<InvalidParse>> warnings = new HashMap<>();

    //column`s and field`s mapper
    private Map<EntityFields, Integer> headersToCols = new EnumMap<>(EntityFields.class);

}
