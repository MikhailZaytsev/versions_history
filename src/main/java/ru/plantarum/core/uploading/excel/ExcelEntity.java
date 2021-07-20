package ru.plantarum.core.uploading.excel;

import lombok.*;
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
    private Long idCounterAgent;
    private String tempFileName;
    private List<String> headers;
    private Long idCampaign;
    private Long idOrganType;
    private Long idTradeMark;

    private List<Product> products = new ArrayList<>();
    private Map<Integer, List<InvalidParse>> errors = new HashMap<>();
    private Map<Integer, List<InvalidParse>> warnings = new HashMap<>();
    private Map<EntityFields, Integer> headersToCols = new EnumMap<>(EntityFields.class);
}
