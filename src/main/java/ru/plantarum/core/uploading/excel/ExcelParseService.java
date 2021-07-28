package ru.plantarum.core.uploading.excel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.service.*;
import ru.plantarum.core.uploading.response.InvalidParse;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExcelParseService {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    private final ExcelBookService excelBookService;
    private final CounterAgentService counterAgentService;
    private final CampaignService campaignService;
    private final TradeMarkService tradeMarkService;
    private final OrganTypeService organTypeService;
    private final ProductService productService;
    private final BareCodeService bareCodeService;
    private final PriceBuyPreliminarilyService priceBuyPreliminarilyService;
    private final PriceSaleService priceSaleService;

    public String getJSON(List list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO create error messages
    public ExcelEntity parseToDb(ExcelEntity excelEntity) {

        XSSFWorkbook book = excelBookService.getBook(excelEntity.getTempFileName());

        if (book == null) {
            addError(excelEntity, 0, Product.class,  "Файл для записи был утерян");
            return excelEntity; // error if tempFile is destroyed
        }

        Sheet sheet = book.getSheet(excelBookService.SOURCE_SHEET_NAME);

        checkExcelEntity(excelEntity);
        if (!excelEntity.getErrors().isEmpty()) {
            return excelEntity; //error if excelEntity not correct
        }

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            createProduct(excelEntity, row);
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.EAN13)) {
                createBareCode(excelEntity, row);
            }
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_IN)) {
                createPriceBuyPreliminarily(excelEntity, row);
            }
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_OUT)) {
                createPriceSale(excelEntity, row);
            }
        }
        System.err.println("here");
//        if (excelEntity.getErrors().isEmpty()) {
//            //TODO add saving method
//        }
        return excelEntity;
    }

    private void checkExcelEntity(ExcelEntity excelEntity) {
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.PRODUCT_NAME)) {
            addError(excelEntity, 0, Product.class, "Не установлена колонка для наименования продукта");
        }
        if (!excelEntity.getTradeMark().getTradeMarkName().equalsIgnoreCase("Из excel")) {
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.TRADEMARK)) {
                addError(excelEntity, 0, Product.class, "Торговая марка установлена и из БД, и из excel");
            }
            TradeMark tradeMark = tradeMarkService.findByTradeMarkName(excelEntity.getTradeMark().getTradeMarkName());
            if (tradeMark != null) {
                excelEntity.setTradeMark(tradeMark);
            } else {
                addError(excelEntity, 0, Product.class, "Торговой марки с таким именем не существует в БД");
            }
        } else {
            if (!excelEntity.getHeadersToCols().containsKey(EntityFields.TRADEMARK)) {
                addError(excelEntity, 0, Product.class, "Не установлен выбор торговой марки");
            }
        }
        if (!excelEntity.getOrganType().getOrganTypeName().equalsIgnoreCase("Из excel")) {
            if (excelEntity.getHeadersToCols().containsKey(EntityFields.ORGAN_TYPE)) {
                addError(excelEntity, 0, Product.class, "Тип органа установлен и из БД, и из Excel");
            }
            OrganType organType = organTypeService.findByOrganTypeName(excelEntity.getOrganType().getOrganTypeName());
            if (organType != null) {
                excelEntity.setOrganType(organType);
            } else {
                addError(excelEntity, 0, Product.class, "Типа органа с таким именем не существует в БД");
            }
        } else {
            if (!excelEntity.getHeadersToCols().containsKey(EntityFields.ORGAN_TYPE)) {
                addError(excelEntity, 0, Product.class, "Не установлен выбор типа органа");
            }
        }
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_OUT) || excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_IN)) {
            Campaign campaign = campaignService.getOne(excelEntity.getCampaign().getIdCampaign()).orElse(null);
            if (campaign != null) {
                excelEntity.setCampaign(campaign);
            } else {
                addError(excelEntity, 0, PriceBuyPreliminarily.class, (String.format("Кампании с таким id: %d не существует в БД", excelEntity.getCampaign().getIdCampaign())));
                addError(excelEntity, 0, PriceSale.class, (String.format("Кампании с таким id: %d не существует в БД", excelEntity.getCampaign().getIdCampaign())));
            }
        }
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.PRICE_IN)) {
            CounterAgent counterAgent = counterAgentService.getOne(excelEntity.getCounterAgent().getIdCounterAgent()).orElse(null);
            if (counterAgent != null) {
                excelEntity.setCounterAgent(counterAgent);
            } else {
                addError(excelEntity, 0, PriceBuyPreliminarily.class, (String.format("Контрагента с таким id: %d не существует в БД", excelEntity.getCounterAgent().getIdCounterAgent())));
            }
        }
        if (!excelEntity.getHeadersToCols().containsKey(EntityFields.NUMBER_IN_PACK)) {
            addWarning(excelEntity, 0, Product.class, "Не выбрано значение \"кол-во в упаквке\", все продукты сохранятся с пустым полем");
        }
    }

    private void createProduct(ExcelEntity excelEntity, Row row) {
        Product product = new Product();
        product.setProductName(excelBookService.getStringFromCell(row, excelEntity.getHeadersToCols().get(EntityFields.PRODUCT_NAME)));
        product.setTradeMark(createTradeMark(excelEntity, row));
        product.setOrganType(createOrganType(excelEntity, row));
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.NUMBER_IN_PACK)) {
            product.setNumberInPack(createNumberInPack(excelEntity, row));
        }
        checkProduct(excelEntity, product, row);
    }

    private TradeMark createTradeMark(ExcelEntity excelEntity, Row row) {
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.TRADEMARK)) {
            TradeMark tradeMark = excelEntity.getTradeMark();
            String tradeMarkName = excelBookService.getStringFromCell(row, excelEntity.getHeadersToCols().get(EntityFields.TRADEMARK));
            if (tradeMark.getIdTradeMark() == null ||
                    !tradeMarkName.equalsIgnoreCase(tradeMark.getTradeMarkName())) {
                tradeMark = tradeMarkService.findByTradeMarkName(tradeMarkName);
            }
            if (tradeMark == null) {
                addError(excelEntity, row.getRowNum(), Product.class, String.format("Торговой марки с названием %s в БД не существует", tradeMarkName));
            } else {
                excelEntity.setTradeMark(tradeMark);
            }
            return tradeMark;
        } else {
            return excelEntity.getTradeMark();
        }
    }

    private OrganType createOrganType(ExcelEntity excelEntity, Row row) {
        if (excelEntity.getHeadersToCols().containsKey(EntityFields.ORGAN_TYPE)) {
            OrganType organType = excelEntity.getOrganType();
            String organTypeName = excelBookService.getStringFromCell(row, excelEntity.getHeadersToCols().get(EntityFields.ORGAN_TYPE));
            if (organType.getIdOrganType() == null ||
                    !organTypeName.equalsIgnoreCase(organType.getOrganTypeName())) {
                organType = organTypeService.findByOrganTypeName(organTypeName);
            }
            if (organType == null) {
                addError(excelEntity, row.getRowNum(), Product.class, String.format("Типа органа с названием %s в БД не существует", organTypeName));
            } else {
                excelEntity.setOrganType(organType);
            }
            return organType;
        } else {
            return excelEntity.getOrganType();
        }
    }

    private Short createNumberInPack(ExcelEntity excelEntity, Row row) {
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.NUMBER_IN_PACK);
        if (row.getCell(cellIndex).getCellType().equals(CellType.BLANK)) {
            return null;
        }
        try {
            return Short.valueOf(excelBookService.getStringFromCell(row, cellIndex));
        } catch (NumberFormatException e) {
            addError(excelEntity, row.getRowNum(), Product.class, "Невозможно преобразовать в \"кол-во в упаковке\" значение из ячейки");
            return null;
        }
    }

    private void checkProduct(ExcelEntity excelEntity, Product product, Row row) {
        boolean isValid = true;
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        if (!violations.isEmpty()) {
            violations.forEach(violation -> addError(excelEntity, row.getRowNum(), Product.class, violation.getMessage()));
        }
        if (StringUtils.isBlank(product.getProductName()) || product.getTradeMark() == null) {
            isValid = false;
        }
        if (isValid && productService.exists(product)) {
            addWarning(excelEntity, row.getRowNum(), Product.class,
                    String.format("Продукт с названием %s, торговой маркой %s и кол-ом в упаковке %d уже существует",
                            product.getProductName(), product.getTradeMark().getTradeMarkName(), product.getNumberInPack()));
            excelEntity.getExistsProducts().put(row.getRowNum(),
                    productService.findProduct(product));
        }
        for (Map.Entry<Integer, Product> pair : excelEntity.getProducts().entrySet()) {
            Integer key = pair.getKey();
            Product added = pair.getValue();
            if (added.equals(product)) {
                addError(excelEntity, row.getRowNum(), Product.class, String.format("Продукт с такими названием, торговой маркой и кол-ом в упаковке уже был добавлен ранее из строки %d", ++key));
            }
        }
        if (!excelEntity.getExistsProducts().containsKey(row.getRowNum())) {
            excelEntity.getProducts().put(row.getRowNum(), product);
        }
    }

    private void createBareCode(ExcelEntity excelEntity, Row row) {
        BareCode bareCode = new BareCode();
        bareCode.setEan13(excelBookService.getStringFromCell(row, excelEntity.getHeadersToCols().get(EntityFields.EAN13)));
        checkBareCode(excelEntity, bareCode, row);
    }

    private void checkBareCode(ExcelEntity excelEntity, BareCode bareCode, Row row) {
        Set<ConstraintViolation<BareCode>> violations = validator.validate(bareCode);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<BareCode> violation : violations) {
                if (!violation.getMessage().equalsIgnoreCase("Значение продукта не должно быть пустым")) {
                    addError(excelEntity, row.getRowNum(), BareCode.class, violation.getMessage());
                }
            }
        }
        if (excelEntity.getExistsProducts().containsKey(row.getRowNum()) && bareCode.getEan13() != null) {
            Product product = excelEntity.getExistsProducts().get(row.getRowNum());
            if (bareCodeService.exists(product.getIdProduct(), bareCode.getEan13())) {
                addError(excelEntity, row.getRowNum(), BareCode.class,
                        String.format("Пара штрих-код %s и продукт %s уже существуетв  БД",
                                bareCode.getEan13().toString(), product.getProductName()));

            } else {
                bareCode.setProduct(product);
            }
        }
        excelEntity.getBareCodes().put(row.getRowNum(), bareCode);
    }

    private void createPriceBuyPreliminarily(ExcelEntity excelEntity, Row row) {
        PriceBuyPreliminarily priceBuyPreliminarily = new PriceBuyPreliminarily();
        priceBuyPreliminarily.setCounterAgent(excelEntity.getCounterAgent());
        priceBuyPreliminarily.setCampaign(excelEntity.getCampaign());
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRICE_IN);
        priceBuyPreliminarily.setPriceBuy(
                isBigDecimal(excelBookService.getStringFromCell(row, cellIndex), excelEntity, row.getRowNum(), PriceBuyPreliminarily.class)
        );
        checkPriceBuyPreliminarily(excelEntity, priceBuyPreliminarily, row);
    }

    private void checkPriceBuyPreliminarily(ExcelEntity excelEntity, PriceBuyPreliminarily priceBuyPreliminarily, Row row) {
        Set<ConstraintViolation<PriceBuyPreliminarily>> violations = validator.validate(priceBuyPreliminarily);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<PriceBuyPreliminarily> violation : violations) {
                if (!violation.getMessage().equalsIgnoreCase("Значение продукта не должно быть пустым")) {
                    addError(excelEntity, row.getRowNum(), PriceBuyPreliminarily.class, violation.getMessage());
                }
            }
        }
        if (excelEntity.getExistsProducts().containsKey(row.getRowNum()) &&
                priceBuyPreliminarily.getCounterAgent() != null &&
                priceBuyPreliminarily.getCampaign() != null) {
            Product product = excelEntity.getExistsProducts().get(row.getRowNum());
            Long idCounterAgent = priceBuyPreliminarily.getCounterAgent().getIdCounterAgent();
            Long idCampaign = priceBuyPreliminarily.getCampaign().getIdCampaign();
            if (priceBuyPreliminarilyService.exists(product.getIdProduct(), idCounterAgent, idCampaign)) {
                addError(excelEntity, row.getRowNum(), PriceBuyPreliminarily.class,
                        String.format("Пара цена_вход_предв %s и продукт %s уже существуетв  БД",
                                priceBuyPreliminarily.getPriceBuy().toString(), product.getProductName()));
            } else {
                priceBuyPreliminarily.setProduct(product);
            }
        }
        excelEntity.getPriceBuyPreliminarilyMap().put(row.getRowNum(), priceBuyPreliminarily);
    }

    private void createPriceSale(ExcelEntity excelEntity, Row row) {
        PriceSale priceSale = new PriceSale();
        priceSale.setCampaign(excelEntity.getCampaign());
        priceSale.setPropertyPrice("Просто_Продажа");
        priceSale.setPackOnly(false);
        int cellIndex = excelEntity.getHeadersToCols().get(EntityFields.PRICE_OUT);
        priceSale.setPrice(
                isBigDecimal(excelBookService.getStringFromCell(row, cellIndex), excelEntity, row.getRowNum(), PriceSale.class)
        );
        checkPriceSale(excelEntity, priceSale, row);
    }

    private void checkPriceSale(ExcelEntity excelEntity, PriceSale priceSale, Row row) {
        Set<ConstraintViolation<PriceSale>> violations = validator.validate(priceSale);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<PriceSale> violation : violations) {
                if (!violation.getMessage().equalsIgnoreCase("Значение продукта не должно быть пустым")) {
                    addError(excelEntity, row.getRowNum(), PriceSale.class, violation.getMessage());
                }
            }
        }
        if (excelEntity.getExistsProducts().containsKey(row.getRowNum()) && priceSale.getCampaign() != null) {
            Product product = excelEntity.getExistsProducts().get(row.getRowNum());
            Long idCampaign = priceSale.getCampaign().getIdCampaign();
            if (priceSaleService.exists(product.getIdProduct(), priceSale.getPropertyPrice(), idCampaign)) {
                addError(excelEntity, row.getRowNum(), PriceSale.class,
                        String.format("Пара цена_выход_предв %s и продукт %s уже существуетв  БД",
                                priceSale.getPrice().toString(), product.getProductName()));
            }
            else {
                priceSale.setProduct(product);
            }
        }
        excelEntity.getPriceSales().put(row.getRowNum(), priceSale);
    }

    private void addWarning(ExcelEntity excelEntity, int rowIndex, Class<?> c, String warning) {
        if (excelEntity.getWarnings().containsKey(rowIndex)) {
            excelEntity.getWarnings().get(rowIndex).add(new InvalidParse(c.getSimpleName(), warning));
        } else {
            List<InvalidParse> invalidParseList = new ArrayList<>();
            invalidParseList.add(new InvalidParse(c.getSimpleName(), warning));
            excelEntity.getWarnings().put(rowIndex, invalidParseList);
        }
    }

    private void addError(ExcelEntity excelEntity, int rowIndex, Class<?> c, String error) {
        if (excelEntity.getErrors().containsKey(rowIndex)) {
            excelEntity.getErrors().get(rowIndex).add(new InvalidParse(c.getSimpleName(), error));
        } else {
            List<InvalidParse> invalidParseList = new ArrayList<>();
            invalidParseList.add(new InvalidParse(c.getSimpleName(), error));
            excelEntity.getErrors().put(rowIndex, invalidParseList);
        }
    }

    private BigDecimal isBigDecimal(String value, ExcelEntity excelEntity, int rowIndex, Class<?> c) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            addError(excelEntity, rowIndex, c, "Невозможно преобразовать в цену значение из ячейки");
            return null;
        }
    }
}
