package ru.plantarum.core.uploading.excel;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import ru.plantarum.core.entity.BareCode;
import ru.plantarum.core.entity.PriceBuyPreliminarily;
import ru.plantarum.core.entity.PriceSale;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.uploading.response.InvalidParse;

import javax.naming.ldap.HasControls;
import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ExcelEntity {

    //fields from frontend
    private Long idCounterAgent;
    private String tempFileName;
    private List<String> headers;
    private Long idCampaign;
    private Long idOrganType;
    private Long idTradeMark;

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
