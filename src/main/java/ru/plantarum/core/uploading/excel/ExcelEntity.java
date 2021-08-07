package ru.plantarum.core.uploading.excel;

import lombok.*;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.uploading.response.InvalidParse;

import java.util.*;

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

    //variables to show how much of every entity saved
    //or String with result message
    private int lastRowNum = 0;
    private StringBuilder result = new StringBuilder("Всего сохранено:\n");
//    private int productCount = 0;
//    private int bareCodeCount = 0;
//    private int priceBuyCount = 0;
//    private int priceSaleCount = 0;

    //collections for save entities
    private Map<Integer, Product> products = new HashMap<>();
    private Map<Integer, BareCode> bareCodes = new HashMap<>();
    private Map<Integer, PriceBuyPreliminarily> priceBuyPreliminarilyMap = new HashMap<>();
    private Map<Integer, PriceSale> priceSales = new HashMap<>();
    //collection products from DB
    private Map<Integer, Product> existsProducts = new HashMap<>();

    //collections for messages
    private ArrayList<InvalidParse> errors = new ArrayList<>();
    private ArrayList<InvalidParse> warnings = new ArrayList<>();

    //column`s and field`s mapper
    private Map<EntityFields, Integer> headersToCols = new EnumMap<>(EntityFields.class);

}
